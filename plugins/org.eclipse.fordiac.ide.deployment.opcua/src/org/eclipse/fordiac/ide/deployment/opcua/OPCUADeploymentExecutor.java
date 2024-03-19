/*******************************************************************************
 * Copyright (c) 2022, 2024 Markus Meingast, Johannes Kepler University Linz
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Markus Meingast
 *     - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.fordiac.ide.deployment.opcua;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import static org.eclipse.milo.opcua.stack.core.util.ConversionUtil.toList;

import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;
import org.eclipse.fordiac.ide.deployment.IDeviceManagementCommunicationHandler;
import org.eclipse.fordiac.ide.deployment.data.ConnectionDeploymentData;
import org.eclipse.fordiac.ide.deployment.data.FBDeploymentData;
import org.eclipse.fordiac.ide.deployment.devResponse.Response;
import org.eclipse.fordiac.ide.deployment.exceptions.DeploymentException;
import org.eclipse.fordiac.ide.deployment.iec61499.ResponseMapping;
import org.eclipse.fordiac.ide.deployment.interactors.IDeviceManagementInteractor;
import org.eclipse.fordiac.ide.deployment.monitoringbase.MonitoringBaseElement;
import org.eclipse.fordiac.ide.deployment.opcua.helpers.Constants;
import org.eclipse.fordiac.ide.deployment.util.DeploymentHelper;
import org.eclipse.fordiac.ide.deployment.util.IDeploymentListener;
import org.eclipse.fordiac.ide.deployment.util.IDeploymentListener2;
import org.eclipse.fordiac.ide.model.libraryElement.Device;
import org.eclipse.fordiac.ide.model.libraryElement.FBNetworkElement;
import org.eclipse.fordiac.ide.model.libraryElement.IInterfaceElement;
import org.eclipse.fordiac.ide.model.libraryElement.Resource;
import org.eclipse.fordiac.ide.model.libraryElement.StructManipulator;
import org.eclipse.fordiac.ide.model.libraryElement.VarDeclaration;
import org.eclipse.fordiac.ide.ui.FordiacLogHelper;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.SessionActivityListener;
import org.eclipse.milo.opcua.sdk.client.api.UaSession;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseDirection;
import org.eclipse.milo.opcua.stack.core.types.enumerated.BrowseResultMask;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.BrowseDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodRequest;
import org.eclipse.milo.opcua.stack.core.types.structured.CallMethodResult;
import org.eclipse.milo.opcua.stack.core.types.structured.CallResponse;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.eclipse.milo.opcua.stack.core.types.structured.ReferenceDescription;
import org.xml.sax.InputSource;

public class OPCUADeploymentExecutor implements IDeviceManagementInteractor {

	public enum ConnectionStatus {
		CONNECTED, DISCONNECTED, NOT_CONNECTED
	}

	private final OpcUaClient client;
	private ConnectionStatus connectionStatus;
	private final Device device;
	private NodeId resourceNode;

	private final List<CallMethodRequest> requests = new ArrayList<>();
	private final List<String> requestMessages = new ArrayList<>();
	private final List<IDeploymentListener> listeners = new ArrayList<>();

	/* Future for Resource NodeId */
	private CompletableFuture<NodeId> fResourceNode;

	private final HashMap<String, NodeId> availableResources = new HashMap<>();

	private final ResponseMapping respMapping = new ResponseMapping();

	public OPCUADeploymentExecutor(final Device dev, final IDeviceManagementCommunicationHandler overrideHandler) {
		this.device = dev;
		this.client = createClient();
		this.resourceNode = null;
		this.connectionStatus = ConnectionStatus.NOT_CONNECTED;
	}

	protected OpcUaClient createClient() {
		try {
			String mgrId = DeploymentHelper.getMgrID(device);
			mgrId = mgrId.substring(1, mgrId.length() - 1);
			final List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(mgrId).get();
			final OpcUaClientConfigBuilder cfg = new OpcUaClientConfigBuilder();

			cfg.setEndpoint(endpoints.get(0));
			final OpcUaClient newClient = OpcUaClient.create(cfg.build());

			newClient.addSessionActivityListener(new SessionActivityListener() {
				@Override
				public void onSessionActive(final UaSession session) {
					SessionActivityListener.super.onSessionActive(session);
					connectionStatus = ConnectionStatus.CONNECTED;
				}

				@Override
				public void onSessionInactive(final UaSession session) {
					SessionActivityListener.super.onSessionInactive(session);
					connectionStatus = ConnectionStatus.DISCONNECTED;
				}
			});
			return newClient;
		} catch (final Exception e) {
			FordiacLogHelper.logInfo(e.toString());
			e.printStackTrace();
			FordiacLogHelper.logError(Messages.OPCUADeploymentExecutor_CreateClientFailed, e);
		}
		return null;
	}

	protected Device getDevice() {
		return device;
	}

	@Override
	public boolean isConnected() {
		return connectionStatus == ConnectionStatus.CONNECTED;
	}

	@Override
	public void connect() throws DeploymentException {
		try {
			client.connect().get();
			for (final IDeploymentListener listener : listeners) {
				listener.connectionOpened(device);
			}
		} catch (final Exception e) {
			FordiacLogHelper.logInfo(e.getMessage());
			e.printStackTrace();
			throw new DeploymentException(Messages.OPCUADeploymentExecutor_CouldNotConnectToDevice, e);
		}
	}

	@Override
	public void disconnect() throws DeploymentException {
		if (isConnected()) {
			try {
				client.disconnect().get();
				for (final IDeploymentListener listener : listeners) {
					listener.connectionClosed(device);
				}
			} catch (final Exception e) {
				FordiacLogHelper.logInfo(e.getMessage());
				e.printStackTrace();
				throw new DeploymentException(Messages.OPCUADeploymentExecutor_CouldNotDisconnectFromDevice, e);
			}
		}
	}

	/**
	 * Sends single request
	 *
	 * @param destination - Only needed for displaying on Deployment Console
	 **/
	private synchronized CompletableFuture<CallMethodResult> sendREQ(final String destination,
			final CallMethodRequest request, final String message) throws IOException {
		return client.call(request).thenCompose(result -> {
			if (!result.getStatusCode().isGood()) {
				displayCommand(result.getStatusCode(), destination, message);
			}
			return CompletableFuture.completedFuture(result);
		});
	}

	/**
	 * Sends list of requests
	 *
	 * @param destination - Only needed for displaying on Deployment Console
	 */
	private synchronized List<CallMethodResult> sendREQ(final String destination) throws IOException {
		CallResponse response;
		try {
			response = client.call(requests).get();
		} catch (final ExecutionException e) {
			throw new IOException(MessageFormat.format(Messages.OPCUADeploymentExecutor_RequestFailed, destination), e);
		} catch (final InterruptedException e) {
			throw new IOException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_RequestInterrupted, destination), e);
		}
		return handleResponse(response, destination);
	}

	/************************************************************************
	 * Listener commands
	 ************************************************************************/

	@Override
	public void addDeploymentListener(final IDeploymentListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void removeDeploymentListener(final IDeploymentListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	/**************************************************************************
	 * Management commands
	 **************************************************************************/

	@Override
	public void createResource(final Resource resource) throws DeploymentException {
		final String resName = resource.getName();
		final String resType = resource.getTypeName();
		final CallMethodRequest request = new CallMethodRequest(Constants.MGMT_NODE, Constants.CREATE_RESOURCE_NODE,
				new Variant[] { new Variant(resName), new Variant(resType) });
		final String message = MessageFormat.format(Constants.CREATE_RESOURCE_INSTANCE, resName, resType);
		CallMethodResult result = null;
		try {
			result = sendREQ(resName, request, message).get();
		} catch (final IOException e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_CreateResourceFailed, resName), e);
		} catch (final Exception e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_DeviceConnectionClosed, resName), e);
		}
		if (result == null) {
			return;
		}
		resourceNode = processResult(result);
	}

	@Override
	public void writeResourceParameter(final Resource resource, final String parameter, final String value)
			throws DeploymentException {
		final String resName = resource.getName();
		final CallMethodRequest request = new CallMethodRequest(Constants.MGMT_NODE, Constants.WRITE_RESOURCE_NODE,
				new Variant[] { new Variant(resName), new Variant(value) });
		final String message = MessageFormat.format(Constants.WRITE_RESOURCE_PARAMETER, value);
		try {
			sendREQ(resName, request, message).get();
		} catch (final IOException e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_WriteResourceFailed, resName), e);
		} catch (final Exception e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_DeviceConnectionClosed, resName), e);
		}
	}

	@Override
	public void writeDeviceParameter(final Device device, final String parameter, final String value)
			throws DeploymentException {
		final String devName = device.getName();
		final CallMethodRequest request = new CallMethodRequest(Constants.MGMT_NODE, Constants.WRITE_DEVICE_NODE,
				new Variant[] { new Variant(value) });
		final String message = MessageFormat.format(Constants.WRITE_DEVICE_PARAMETER, value);
		try {
			sendREQ(devName, request, message).get();
		} catch (final IOException e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_WriteDeviceFailed, devName), e);
		} catch (final Exception e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_DeviceConnectionClosed, devName), e);
		}
	}

	@Override
	public void createFBInstance(final FBDeploymentData fbData, final Resource res) throws DeploymentException {
		if (resourceNode == null) {
			return;
		}
		final String fbType = getValidType(fbData.getFb());
		final String fullFbName = MessageFormat.format("{0}{1}", fbData.getPrefix(), fbData.getFb().getName()); //$NON-NLS-1$
		if ("".equals(fbType)) {
			throw new DeploymentException(MessageFormat
					.format(Messages.OPCUADeploymentExecutor_CreateFBInstanceFailedNoTypeFound, fullFbName));
		}
		final CallMethodRequest request = new CallMethodRequest(resourceNode, Constants.CREATE_FB_NODE,
				new Variant[] { new Variant(fullFbName), new Variant(fbType) });
		final String message = MessageFormat.format(Constants.CREATE_FB_INSTANCE, fullFbName, fbType);
		requests.add(request);
		requestMessages.add(message);
	}

	@Override
	public void writeFBParameter(final Resource resource, final String value, final FBDeploymentData fbData,
			final VarDeclaration varDecl) throws DeploymentException {
		if (resourceNode == null) {
			return;
		}
		final String destination = MessageFormat.format("{0}{1}.{2}", fbData.getPrefix(), fbData.getFb().getName(), //$NON-NLS-1$
				varDecl.getName());
		final CallMethodRequest request = new CallMethodRequest(resourceNode, Constants.WRITE_FB_NODE,
				new Variant[] { new Variant(destination), new Variant(value) });
		final String message = MessageFormat.format(Constants.WRITE_FB_PARAMETER, destination, value);
		requests.add(request);
		requestMessages.add(message);
	}

	@Override
	public void createConnection(final Resource res, final ConnectionDeploymentData connData)
			throws DeploymentException {
		if (resourceNode == null) {
			return;
		}
		final IInterfaceElement sourceData = connData.getSource();
		final IInterfaceElement destinationData = connData.getDestination();

		if (sourceData == null || sourceData.getFBNetworkElement() == null || destinationData == null
				|| destinationData.getFBNetworkElement() == null) {
			throw new DeploymentException(MessageFormat
					.format(Messages.OPCUADeploymentExecutor_CreateConnectionFailedNoDataFound, res.getName()));
		}

		final FBNetworkElement sourceFB = sourceData.getFBNetworkElement();
		final FBNetworkElement destinationFB = destinationData.getFBNetworkElement();
		final String source = MessageFormat.format("{0}{1}.{2}", connData.getSourcePrefix(), sourceFB.getName(), //$NON-NLS-1$
				sourceData.getName());
		final String destination = MessageFormat.format("{0}{1}.{2}", connData.getDestinationPrefix(), //$NON-NLS-1$
				destinationFB.getName(), destinationData.getName());
		final CallMethodRequest request = new CallMethodRequest(resourceNode, Constants.CREATE_CONNECTION_NODE,
				new Variant[] { new Variant(source), new Variant(destination) });
		final String message = MessageFormat.format(Constants.CREATE_CONNECTION, destination, source);
		requests.add(request);
		requestMessages.add(message);
	}

	@Override
	public void startFB(final Resource res, final FBDeploymentData fbData) throws DeploymentException {
		if (resourceNode == null) {
			return;
		}
		final String fullFbName = MessageFormat.format("{0}{1}", fbData.getPrefix(), fbData.getFb().getName()); //$NON-NLS-1$
		final CallMethodRequest request = new CallMethodRequest(resourceNode, Constants.START_FB_NODE,
				new Variant[] { new Variant(fullFbName) });
		final String message = MessageFormat.format(Constants.START_FB, fullFbName);
		try {
			sendREQ(res.getName(), request, message).get();
		} catch (final IOException e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_StartFBFailed, fullFbName), e);
		} catch (final Exception e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_DeviceConnectionClosed, fullFbName), e);
		}
	}

	@Override
	public void startResource(final Resource resource) throws DeploymentException {
		if (resourceNode == null) {
			return;
		}
		final String resourceName = resource.getName();
		final CallMethodRequest request = new CallMethodRequest(Constants.MGMT_NODE, Constants.START_RESOURCE_NODE,
				new Variant[] { new Variant(resourceName) });
		final String message = MessageFormat.format(Constants.START_RESOURCE, resourceName);
		requests.add(request);
		requestMessages.add(message);

		try {
			sendREQ(resourceName);
		} catch (final IOException e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_StartResourceFailed, resourceName), e);
		} catch (final Exception e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_DeviceConnectionClosed, resourceName), e);
		}
		requests.clear();
		requestMessages.clear();
	}

	@Override
	public void startDevice(final Device dev) throws DeploymentException {
		final String devName = dev.getName();
		final CallMethodRequest request = new CallMethodRequest(Constants.MGMT_NODE, Constants.START_DEVICE_NODE,
				new Variant[] {});
		final String message = MessageFormat.format(Constants.START_DEVICE, devName);
		try {
			sendREQ(devName, request, message).get();
		} catch (final IOException e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_StartDeviceFailed, devName), e);
		} catch (final Exception e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_DeviceConnectionClosed, devName), e);
		}
	}

	@Override
	public void deleteResource(final String resName) throws DeploymentException {
		final CallMethodRequest killRequest = new CallMethodRequest(Constants.MGMT_NODE, Constants.KILL_RESOURCE_NODE,
				new Variant[] { new Variant(resName) });
		String message = MessageFormat.format(Constants.KILL_RESOURCE, resName);
		try {
			sendREQ(resName, killRequest, message).get();
		} catch (final IOException e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_KillResourceFailed, resName), e);
		} catch (final Exception e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_DeviceConnectionClosed, resName), e);
		}

		final CallMethodRequest deleteRequest = new CallMethodRequest(Constants.MGMT_NODE,
				Constants.DELETE_RESOURCE_NODE, new Variant[] { new Variant(resName) });
		message = MessageFormat.format(Constants.DELETE_RESOURCE, resName);
		try {
			sendREQ(resName, deleteRequest, message).get();
		} catch (final IOException e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_DeleteResourceFailed, resName), e);
		} catch (final Exception e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_DeviceConnectionClosed, resName), e);
		}
	}

	@Override
	public void deleteFB(final Resource res, final FBDeploymentData fbData) throws DeploymentException {
		if (!getResourcesHandle()) {
			return;
		}

		final String fullFbName = MessageFormat.format("{0}{1}", fbData.getPrefix(), fbData.getFb().getName()); //$NON-NLS-1$
		final String resName = res.getName();
		final CallMethodRequest request = new CallMethodRequest(availableResources.get(resName),
				Constants.DELETE_FB_NODE, new Variant[] { new Variant(fullFbName), });
		final String message = MessageFormat.format(Constants.DELETE_FB_INSTANCE, fullFbName);
		try {
			sendREQ(resName, request, message).get();
		} catch (final IOException e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_DeleteFBFailed, fullFbName), e);
		} catch (final Exception e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_DeviceConnectionClosed, fullFbName), e);
		}
	}

	@Override
	public void deleteConnection(final Resource res, final ConnectionDeploymentData connData)
			throws DeploymentException {
		if (!getResourcesHandle()) {
			return;
		}

		final IInterfaceElement sourceData = connData.getSource();
		final IInterfaceElement destinationData = connData.getDestination();

		if (sourceData == null || sourceData.getFBNetworkElement() == null || destinationData == null
				|| destinationData.getFBNetworkElement() == null) {
			throw new DeploymentException(Messages.OPCUADeploymentExecutor_CreateConnectionFailedNoDataFound);
		}

		final FBNetworkElement sourceFB = sourceData.getFBNetworkElement();
		final FBNetworkElement destinationFB = destinationData.getFBNetworkElement();
		final String source = MessageFormat.format("{0}{1}.{2}", connData.getSourcePrefix(), sourceFB.getName(), //$NON-NLS-1$
				sourceData.getName());
		final String destination = MessageFormat.format("{0}{1}.{2}", connData.getDestinationPrefix(), //$NON-NLS-1$
				destinationFB.getName(), destinationData.getName());
		final String resName = res.getName();
		final CallMethodRequest request = new CallMethodRequest(availableResources.get(resName),
				Constants.DELETE_CONNECTION_NODE, new Variant[] { new Variant(source), new Variant(destination) });
		final String message = MessageFormat.format(Constants.DELETE_CONNECTION, destination, source);
		try {
			sendREQ(resName, request, message).get();
		} catch (final IOException e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_DeleteConnectionFailed, destination, source),
					e);
		} catch (final Exception e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_DeviceConnectionClosed, destination), e);
		}
	}

	@Override
	public void killDevice(final Device dev) throws DeploymentException {
		final String devName = dev.getName();
		final CallMethodRequest request = new CallMethodRequest(Constants.MGMT_NODE, Constants.KILL_DEVICE_NODE,
				new Variant[] {});
		final String message = MessageFormat.format(Constants.KILL_DEVICE, devName);
		try {
			sendREQ(devName, request, message).get();
		} catch (final IOException e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_KillDeviceFailed, devName), e);
		} catch (final Exception e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_DeviceConnectionClosed, devName), e);
		}
	}

	@Override
	public List<org.eclipse.fordiac.ide.deployment.devResponse.Resource> queryResources() throws DeploymentException {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}

	@Override
	public Response readWatches() throws DeploymentException {
		final CallMethodRequest request = new CallMethodRequest(Constants.MGMT_NODE, Constants.READ_WATCHES_NODE,
				new Variant[] {});
		final String message = Constants.READ_WATCHES;
		try {
			return parseWatchesResponse(sendREQ("", request, message).get()); //$NON-NLS-1$
		} catch (final IOException | ExecutionException | InterruptedException e) {
			throw new DeploymentException(Messages.OPCUADeploymentExecutor_ReadWatchesFailed, e);
		}
	}

	@Override
	public void addWatch(final MonitoringBaseElement element) throws DeploymentException {
		if (availableResources.isEmpty() && !getResourcesHandle()) {
			return;
		}
		final String fullFbName = element.getQualifiedString();
		final String resName = element.getResourceString();
		final CallMethodRequest request = new CallMethodRequest(availableResources.get(resName),
				Constants.ADD_WATCH_NODE, new Variant[] { new Variant(fullFbName) });
		final String message = MessageFormat.format(Constants.ADD_WATCH, fullFbName);
		CallMethodResult result;
		try {
			result = sendREQ(resName, request, message).get();
		} catch (final IOException | InterruptedException | ExecutionException e) {
			throw new DeploymentException(
					MessageFormat.format(Messages.OPCUADeploymentExecutor_AddWatchFailed, fullFbName), e);
		}
		element.setOffline(Constants.MGM_RESPONSE_UNKNOWN.equals(getIEC61499Status(result.getStatusCode())));
	}

	@Override
	public void removeWatch(final MonitoringBaseElement element) throws DeploymentException {
		// TODO Auto-generated method stub

	}

	@Override
	public void triggerEvent(final MonitoringBaseElement element) throws DeploymentException {
		// TODO Auto-generated method stub

	}

	@Override
	public void forceValue(final MonitoringBaseElement element, final String value) throws DeploymentException {
		// TODO Auto-generated method stub

	}

	@Override
	public void clearForce(final MonitoringBaseElement element) throws DeploymentException {
		// TODO Auto-generated method stub

	}

	/**************************************************************************
	 * Helper Methods
	 **************************************************************************/

	/**
	 * Browses Resource Nodes and caches available Resources
	 *
	 * @return CompletableFuture of Browse result status
	 */
	private CompletableFuture<StatusCode> browseResources() {
		final BrowseDescription browse = new BrowseDescription(Constants.MGMT_NODE, BrowseDirection.Forward,
				Identifiers.References, true, uint(NodeClass.Object.getValue()), uint(BrowseResultMask.All.getValue()));
		return client.browse(browse).thenCompose(result -> {
			final List<ReferenceDescription> references = toList(result.getReferences());
			for (final ReferenceDescription rd : references) {
				rd.getNodeId().toNodeId(client.getNamespaceTable())
						.ifPresent(nodeId -> availableResources.put(rd.getBrowseName().getName(), nodeId));
			}
			return CompletableFuture.completedFuture(result.getStatusCode());
		});
	}

	private boolean getResourcesHandle() throws DeploymentException {
		StatusCode status = StatusCode.BAD;
		try {
			status = browseResources().get();
		} catch (InterruptedException | ExecutionException e) {
			throw new DeploymentException(Messages.OPCUADeploymentExecutor_BrowseOPCUAFailed);
		}
		return status.isGood() && !availableResources.isEmpty();
	}

	private String getDestinationInfo(final String destination) {
		String info = client.getConfig().getEndpoint().getEndpointUrl();
		if (!destination.equals("")) { //$NON-NLS-1$
			info += ": " + destination; //$NON-NLS-1$

		}
		return info;
	}

	private Response parseWatchesResponse(final CallMethodResult result) throws IOException {
		if ((result != null && result.getStatusCode().isGood())
				&& (result.getOutputArguments()[0].getValue() instanceof final String response)) {
			return parseXMLResponse(MessageFormat.format(Constants.WATCHES_RESPONSE, response));
		}
		return Constants.EMPTY_WATCHES_RESPONSE;
	}

	private Response parseXMLResponse(final String encodedResponse) throws IOException {
		if (null != encodedResponse) {
			final InputSource source = new InputSource(new StringReader(encodedResponse));
			final XMLResource xmlResource = new XMLResourceImpl();
			xmlResource.load(source, respMapping.getLoadOptions());
			for (final EObject object : xmlResource.getContents()) {
				if (object instanceof final Response response) {
					return response;
				}
			}
		}
		return Constants.EMPTY_WATCHES_RESPONSE;
	}

	private static NodeId processResult(final CallMethodResult result) {
		final StatusCode statusCode = result.getStatusCode();
		if (statusCode.isGood()) {
			return (NodeId) result.getOutputArguments()[0].getValue();
		}
		final StatusCode[] inputArgumentResults = result.getInputArgumentResults();
		if (inputArgumentResults != null) {
			for (int i = 0; i < inputArgumentResults.length; i++) {
				FordiacLogHelper
						.logInfo(MessageFormat.format("Input Argument Result {0}: {1}", i, inputArgumentResults[i])); //$NON-NLS-1$
			}
		}
		return null;
	}

	private List<CallMethodResult> handleResponse(final CallResponse response, final String destination) {
		final List<CallMethodResult> results = Arrays.asList(response.getResults());
		if (results.size() != requestMessages.size()) {
			FordiacLogHelper.logInfo("Result list size does not match number of requests!"); //$NON-NLS-1$
		}
		for (int i = 0; i < results.size(); i++) {
			final CallMethodResult result = results.get(i);
			final StatusCode opcuaStatus = result.getStatusCode();
			if (!opcuaStatus.isGood()) {
				final String message = requestMessages.get(i);
				displayCommand(opcuaStatus, destination, message);
			}
		}
		return results;
	}

	private void displayCommand(final StatusCode opcuaStatus, final String destination, final String message) {
		final String info = getDestinationInfo(destination);
		final String responseMessage = MessageFormat.format(Constants.RESPONSE, getIEC61499Status(opcuaStatus));
		for (final IDeploymentListener listener : listeners) {
			listener.postCommandSent(info, destination, message);
		}
		for (final IDeploymentListener listener : listeners) {
			if (listener instanceof final IDeploymentListener2 listener2) {
				listener2.postResponseReceived(info, message, responseMessage, destination);
			} else {
				listener.postResponseReceived(responseMessage, destination);
			}
		}
	}

	private static String getIEC61499Status(final StatusCode opcuaStatus) {
		final String status = Constants.RESPONSE_MAP.get(opcuaStatus.getValue());
		if (status != null) {
			return status;
		}
		FordiacLogHelper.logInfo(
				MessageFormat.format(Messages.OPCUADeploymentExecutor_UnknownResponseCode, opcuaStatus.toString()));
		return Constants.MGM_RESPONSE_UNKNOWN;
	}

	private static String getValidType(final FBNetworkElement fb) {
		if (fb != null && fb.getTypeEntry() != null) {
			if (fb instanceof StructManipulator) {
				return MessageFormat.format("{0}_1{2}", fb.getTypeName(), //$NON-NLS-1$
						((StructManipulator) fb).getStructType().getName());
			}
			return fb.getTypeName();
		}
		return null;
	}
}