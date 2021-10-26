import { Client, IFrame, IMessage } from '@stomp/stompjs';
import { CloseEvent } from 'sockjs-client';

type IFrame2any = ((frame: IFrame) => any)
type CloseEvent2any = ((event: CloseEvent) => any)
type Event2any = ((event: Event) => any)
type IMessage2any = ((response: IMessage) => any)

export default class WebsocketService {
    client: Client;

    /**
     * WebsocketService constructor
     * 1. Create new websocket Client object and connect to brokerURL
     * @param brokerurl - Server's broker URL
     * @param reconnectdelay - Time interval to reconnect if fail to connect
    */
    constructor(brokerurl: string, reconnectdelay: number = 5000) {
        this.client = new Client({ brokerURL: brokerurl, reconnectDelay: reconnectdelay })
    }

    /** Connect client to broker */
    connect = () : void => {
        this.client.activate();
    }

    /** Disconnect client from broker */
    disconnect = () : void => {
        this.client.forceDisconnect();
        this.client.deactivate();
    }

    /**
     * Set websocket onConnect callback function
     * @param callback - onConnect callback function
     */
    setonConnectCallback = (callback: IFrame2any) : void => {
        this.client.onConnect = (frame) => callback(frame);
    }

    /** Remove onConnect callback function / Set to do nothing function */
    removeonConnectCallback = () : void => {
        this.client.onConnect = (frame) => this.donothing();
    }

    /**
     * Set websocket onDisconnect callback function
     * @param callback - onDisconnect callback function
    */
    setonDisconnectCallback = (callback: IFrame2any) : void => {
        this.client.onDisconnect = (frame) => callback(frame);
    }

    /** Remove onDisconnect callback function */
    removeonDisconnectCallback = () : void => {
        this.client.onDisconnect = (frame) => this.donothing();
    }

    /**
     * Set websocket onUnhandledFrame callback function
     * @param callback - onUnhandledFrame callback function
    */
    setonUnhandledFrameCallback = (callback: IFrame2any) : void => {
        this.client.onUnhandledFrame = (frame) => callback(frame);
    }

    /** Remove onUnhandledFrame callback function / Set to do nothing function */
    removeonUnhandledFrameCallback = () : void => {
        this.client.onUnhandledFrame = (frame) => this.donothing();
    }

    /**
     * Set websocket onUnhandledMessage callback function
     * @param callback - onUnhandledMessage callback function
    */
    setonUnhandledMessageCallback = (callback: IFrame2any) : void => {
        this.client.onUnhandledMessage = (frame) => callback(frame);
    }

    /** Remove onUnhandledMessage callback function / Set to do nothing function */
    removeonUnhandledMessageCallback = () : void => {
        this.client.onUnhandledMessage = (frame) => this.donothing();
    }

    /**
     * Set websocket onWebSocketClose callback function
     * @param callback - onWebSocketClose callback function
    */
    setonWebSocketCloseCallback = (callback: CloseEvent2any ) : void => {
        this.client.onWebSocketClose = (event) => callback(event);
    }

    /** Remove onWebSocketClose callback function / Set to do nothing function */
    removeonWebSocketCloseCallback = () : void => {
        this.client.onWebSocketClose = (event) => this.donothing();
    }

    /**
     * Set websocket onWebSocketError callback function
     * @param callback - onWebSocketError callback function
    */
    setonWebsocketErrorCallback = (callback: Event2any) : void => {
        this.client.onWebSocketError = (event) => callback(event);
    }

    /** Remove onWebSocketError callback function / Set to do nothing function */
    removeonWebsocketErrorCallback = () : void => {
        this.client.onWebSocketError = (event) => this.donothing();
    }

    /**
     * Subscribe to topic
     * @param destination - Topic to subscribe
     * @param callback - Subscription callback
    */
    subscribe = (destination: string, callback: IMessage2any) : void => {
        this.client.subscribe(destination, callback);
    }

    /**
     * Unsubscribe from topic
     * @param destination - Topic to unsubscribe
    */
    unsubscribe = (destination: string) : void => {
        this.client.unsubscribe(destination);
    }

    /**
     * Send a message to target destination
     * @param destination - Destination to send message/data
     * @param body - Message/data
    */
    sendmessage = (destination: string, body: string) : void => {
        this.client.publish({ destination, body });
    }

    /**
     * Check if websocket client is connected to broker
     * @returns True if client is connected, else false
    */
    isConnected = () : boolean => {
        return this.client.connected;
    }

    /** Simple do nothing function */
    donothing = () => {

    }

}