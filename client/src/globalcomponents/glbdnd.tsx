import React, {Component, DragEvent} from 'react'
import ReactFlow, {ReactFlowProps, addEdge, removeElements, Background, isNode, isEdge,
    FlowElement, Edge, Elements, Connection, OnLoadParams, ArrowHeadType, Node} 
from 'react-flow-renderer'
import {Dictionary} from "./interfaces"

/**
 * DNDSingletab props
 * preparenodedata: Function that responsible to prepare node's data
 * setnumberofelementstosend: Callback function to set the total number of elements to save (autosave)
 * sendnodedataforsaving: Callback function to send node data for saving (autosave)
 * sendedgedataforsaving: Callback function to send edge data for saving (autosave)
*/
interface DragNDropProps extends ReactFlowProps {
    preparenodedata: ((nodetype: string) => Dictionary);
    setnumberofelementstosend: ((count: number) => void);
    sendnodedataforsaving: ((node: Node) => void);
    sendedgedataforsaving: ((edge: Edge) => void);
}

/**
 * DNDSingletab states
 * rfinstance: Instance of ReactFlow
 * elements: Array which store all elements (node & edge) in a flow
*/
interface DragNDropStates {
    RFInstance: OnLoadParams | null;
    elements: FlowElement[];
}

export default class DragNDrop extends Component <DragNDropProps, DragNDropStates> {
    static defaultProps = {
        elements: []
    }

    /**
     * DNDSingletab class properties
     * RFWrapperRef: Reactflow wrapper's ref
     * currentnodeId: Current Id of node (Unique + Auto-increment)
     */
    RFWrapperRef : React.RefObject<any>;
    currentnodeId : number;

    constructor (props: DragNDropProps) {
        super(props);
        this.state = {
            RFInstance: null,
            elements : this.props.elements, 
        }
        this.RFWrapperRef = React.createRef();
        this.currentnodeId = 0;
    }

    /**
     * [COMPONENT LIFECYCLE]
     * Called before component is unmounted
     * 1. Set the number of elements (edge + node) to save in the flow
     * 2. Loop through each element and send the element's data to server to save
    */
    componentWillUnmount = () : void => {
        this.props.setnumberofelementstosend(this.state.elements.length);
        for(let index = 0; index < this.state.elements.length; index ++) {
            let element = this.state.elements[index];
            if (isNode(element))
                this.props.sendnodedataforsaving(element);
            else
                this.props.sendedgedataforsaving(element);
        }
    }

    /**
     * Restore previously saved element into the flow
     * 1. If element is a node, reset node.data.error to BOOLEAN (previously string)
     *      - Update value of currentnodeId to max (node.id, currentnodeId) -> Ensure all nodes' id will be unique
     * @param element - Previously store element
     */
    addLoadedElement = (element: FlowElement) : void => {
        if (isNode(element)) {
            this.currentnodeId = Math.max(Number(element.id), this.currentnodeId);
            element.data.error = false;
        }
        this.setState({ elements: [ ...this.state.elements, element ]})
    }

    /**
     * Generate unique Id for node
     * @returns Increment and return
    */
    getId = () : string => {
        this.currentnodeId++;
        return this.currentnodeId.toString();
    }

    /**
     * [REACTFLOW CALLBACK]
     * Called when a new connection (edge) is created
     * 1. Remove invalid edges if any exists (check edgeValidationChecker function for conditions)
     * @param params - New edge
    */
    onConnect = (params: Edge<any> | Connection) : void => {
        this.setState(
            {
                // type: smoothstep
                elements: addEdge( { ...params, type: "arrowclosed", style: { strokeWidth:3, stroke:"grey" }, arrowHeadType: ArrowHeadType.ArrowClosed }, 
                    this.removeInvalidEdges(params))
            }
        )
    }

    /**
     * Loop through all edges and filter out invalid edge(s)
     * @param newedge - New edge
     * @returns - Elements (array) which contains only valid edges (nodes will not be checked!)
    */
    removeInvalidEdges = (newedge: Edge<any> | Connection) : FlowElement[] => {
        return this.state.elements.filter((element) => isEdge(element)? this.edgeValidationChecker(element, newedge) : true)
    }

    /**
     * Check if an existing edge is valid if new edge is added 
     * Condition 1 - One outgoing edge per node
     * Condition 2 - Simple cycle is not allowed (A -> B and B -> A)
     *      (** NOTE THAT complex cycle is not checked, eg. A -> B, B -> C and C -> A)
     * @param existingedge - Existing edge
     * @param newedge - New edge
     * @returns If existing edge is valid
    */
    edgeValidationChecker = (existingedge: Edge<any> | Connection, newedge: Edge<any> | Connection) : boolean => {
        if (newedge.source === existingedge.source) return false;
        if (newedge.source === existingedge.target && newedge.target === existingedge.source) return false;
        return true;
    }

    /**
     * [REACTFLOW CALLBACK]
     * Called when element(s) is/are removed from flow
     * 1. Remove element from dndelements (state)
     * @param elementsToRemove 
    */
    onElementsRemove = (elementsToRemove: Elements) : void => {
        this.setState( { elements : removeElements(elementsToRemove, this.state.elements ) } )
    }

    /**
     * [REACTFLOW CALLBACK]
     * Called when reactflow is loaded
     * 1. Update reactflow instance
     * @param RFInstance 
    */
    onLoad = (RFInstance: OnLoadParams) : void => {
        this.setState( { RFInstance } )
    }

    /**
     * [REACTFLOW CALLBACK]
     * Called when new node (dragged from toolbar) is dropped
     * 1. Get the node type
     * 2. Calculate the position to place the node
     * 3. Prepare node data
     * 4. Generate new node
     * 5. Add node to dndelements (state)
     * @param event 
    */
    onDrop = (event: DragEvent<HTMLDivElement>) : void => {
        let reactFlowBounds = this.RFWrapperRef.current.getBoundingClientRect();
        let type = event.dataTransfer.getData("application/reactflow");
        if (this.state.RFInstance !== null) {
            let position = this.state.RFInstance.project({
                x: event.clientX - reactFlowBounds.left,
                y: event.clientY - reactFlowBounds.top,
            })
            let data = this.props.preparenodedata(type);
            data['error'] = false;
            let newNode = {id: this.getId(), type, position, data}
            this.setState({ elements : [ ...this.state.elements, newNode ]} );
        }
    }

    /**
     * [REACTFLOW CALLBACK]
     * Called when a node is finished dragging
     * 1. Update node's position (state)
     * @param event 
     * @param node - Node that has been dragged
    */
    onNodeDragStop = (event: React.MouseEvent<Element, MouseEvent>, node: Node<any>) : void => {
        let nodeindex = this.findNodeIndex(node.id);
        this.setState({ 
            elements: [
                ...this.state.elements.slice(0, nodeindex),
                { ...this.state.elements[nodeindex], position: node.position },
                ...this.state.elements.slice(nodeindex + 1)
            ]
        })
    }

    /**
     * [REACTFLOW CALLBACK]
     * Called when drag is over
     * @param event 
    */
    onDragOver = (event: DragEvent<HTMLDivElement>) : void => {
        event.preventDefault();
        event.dataTransfer.dropEffect = "move";
    }

   /**
     * Find the index of node in elements (state) using its nodeId
     * @param nodeid - Id of node
     * @returns - Index of node in elements (state), -1 if not found
    */
    findNodeIndex = (nodeid: string) : number => {
        return this.state.elements.findIndex( object => object.id === nodeid);
    }

    /**
     * Find the first occurance of node type (Iterate through elements (state) array)
     * @param nodetype 
     * @returns Return the nodeId if found, else return null
    */
    searchFirstOccuranceOfNodeType = (nodetype: string) : string | null => {
        for(let index = 0; index < this.state.elements.length; index++) {
            if (this.state.elements[index].type === nodetype)
                return this.state.elements[index].id;
        }
        return null;
    }

    /**
     * Get the entire sequence (array of nodes) starting from specific nodeId
     * A -> B -> C -> D will output [A, B, C, D]
     * @param startnodeId - Starting nodeId of a sequence
     * @returns - Array of nodes in sequence (in order)
    */
    getEntireSequence = (startnodeId: string) : Elements => {
        let sequence : Elements = [];
        let nextnode = this.findConnectedNode(startnodeId);
        while (nextnode !== null) {
            sequence.push(nextnode);
            nextnode = this.findConnectedNode(nextnode.id);
        }
        return sequence;
    }

    /**
     * Find the next node which is connected to a node
     * For example, if A -> B -> C, 
     *      - findConnectedNode(A) will return B
     *      - findConnectedNode(C) will return null
     * @param nodeId - id of a node
     * @returns Node connected to node with its id = nodeId
    */
    findConnectedNode = (nodeid: string) : FlowElement | null => {
        for(let index = 0; index < this.state.elements.length; index++) {
            if (isEdge( this.state.elements[index]) && this.state.elements[index].hasOwnProperty("source")) {
                if (this.state.elements[index]['source'] === nodeid)
                    return this.getFlowElementById(this.state.elements[index]['target']);
            }
        }
        return null;
    }

    /**
     * Get the Element/Node object using Id
     * If id is not found, return null
     * @param nodeId 
    */
    getFlowElementById = (nodeid: string) : FlowElement | null => {
        for(let index = 0; index < this.state.elements.length; index++) {
            if (this.state.elements[index]['id'] === nodeid)
                return this.state.elements[index];
        }
        return null;
    }

    /**
     * Set background of node with id = nodeId to red (error node)
     * 1. Find its index and update (node.data.error) to true
     * @param nodeId - Id of error node
     */
    setErrorNode = (nodeid : string) : void => {
        let nodeindex = this.findNodeIndex(nodeid);
        this.setState ({
            elements:
            [
                ...this.state.elements.slice(0, nodeindex),
                {
                    ...this.state.elements[nodeindex],
                    data: { ...this.state.elements[nodeindex].data, error: true }
                },
                ...this.state.elements.slice(nodeindex + 1)
            ]
        })
    }

    /**
     * Set background of all node to default
     * 1. Reset (node.data.error) of ALL nodes to false
    */
    resetErrorNode = () : void => {
        let elements_copy = this.state.elements;
        for (let index = 0; index < elements_copy.length; index ++) {
            if (isNode(elements_copy[index]))
                elements_copy[index].data.error = false;
        }
        this.setState({ elements : elements_copy })
    }

    /**
     * Update the data of node which its id = nodeId
     * @param nodeId - Id of node
     * @param data - Latest updated (modified) data
    */
    updatenodedata = (nodeid: string, data: Dictionary) : void => {
        let nodeindex = this.findNodeIndex(nodeid)
        this.setState ({
            elements: 
            [
                ...this.state.elements.slice(0, nodeindex),
                {
                    ...this.state.elements[nodeindex], 
                    data: data
                },
                ...this.state.elements.slice(nodeindex + 1)
            ]
        })
    }

    render = () => {
        return (
            <div ref={this.RFWrapperRef} style={{ flexGrow:1, height: '100%' }}>
                <ReactFlow
                    elements={this.state.elements}
                    nodeTypes={this.props.nodeTypes}
                    zoomOnDoubleClick={false}
                    zoomOnScroll={false}
                    zoomOnPinch={false}
                    defaultZoom={1}
                    snapToGrid={true}
                    onConnect={this.onConnect}
                    onElementsRemove={this.onElementsRemove}
                    onLoad={this.onLoad}
                    onDrop={this.onDrop}
                    onDragOver={this.onDragOver}
                    onNodeDoubleClick={this.props.onNodeDoubleClick}
                    onNodeDragStop={this.onNodeDragStop}
                >
                    <Background />
                </ReactFlow>
            </div>
        )
    }
}