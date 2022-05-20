import React, { Component, DragEvent, MouseEvent as ReactMouseEvent } from 'react'
import ReactFlow, { NodeTypesType, addEdge, removeElements, Background, isNode, isEdge,
    FlowElement, Edge, Elements, Connection, OnLoadParams, ArrowHeadType, Node} 
from 'react-flow-renderer'
import { Modal, ModalHeader, ModalBody, ModalFooter, Form, FormGroup, FormFeedback, Label, Input, Button} from 'reactstrap'
import {Dictionary} from "./interfaces"
import "./glbdnd.css"


/**
 * DNDMultitab props
 * nodeTypes: Custom node types
 * preparenodedata: Function that responsible to prepare node's data
 * flowdatacansave: If flow can be saved at the moment (False if not connected to server)
 * unmountwithoutsaving: Callback function which allow component to unmount without autosaving flow
 * setnumberofelementstosend: Callback function to set the total number of elements to save (autosave)
 * sendnodedataforsaving: Callback function to send node data for saving (autosave)
 * sendedgedataforsaving: Callback function to send edge data for saving (autosave)
 * manualsaveflow: Callback function to save/export particular flow to local directory
 * onNodeDoubleClick: Callback function when user double-clicked on a node 
*/
interface DNDTabsProps {
    nodeTypes: NodeTypesType;
    preparenodedata: (nodetype: string) => Dictionary;
    flowdatacansave: boolean;
    unmountwithoutsaving: () => void;
    setnumberofelementstosend: (count: number) => void;
    sendnodedataforsaving: (tabindex: number, node: Node) => void;
    sendedgedataforsaving: (tabindex: number, edge: Edge) => void;
    manualloadflow:(directory: string, filename: string) => void;
    manualsaveflow: (elements: Elements, directory: string, filename: string) => void;
    onNodeDoubleClick: (event: ReactMouseEvent, node: Node) => void;
}

/**
 * DNDMultitab states
 * rfinstance: Instance of ReactFlow
 * activetab: Index of active tab
 * dndelements: Array used to store elements of ALL tabs
 * savemodalisactive: If saveflow modal is active (Modal which pops out when user clicks on SAVE button)
*/
interface DNDTabsStates {
    rfInstance: OnLoadParams | null;
    activetab: number;
    dndelements: Elements[];
    savemodalisactive: boolean;
    loadmodalisactive: boolean;
}

// This class is the only one accessible by cnnmultitab
export default class DragNDropTabs extends Component <DNDTabsProps, DNDTabsStates> {

    /**
     * DNDMultitab class properties
     * rfwrapperref: Reactflow wrapper's ref
     * currentnodeId: Current Id of node (Unique + Auto-increment)
     * flowindextosave: Index of flow which is going to be saved 
    */
    rfwrapperref : React.RefObject<any>;
    currentnodeId: number;
    flowindextosave: number;

    constructor(props: DNDTabsProps) {
        super(props);
        this.state = {
            rfInstance: null,
            activetab: 0,  
            dndelements: [ [] ],
            savemodalisactive: false,
            loadmodalisactive: false
        }
        this.rfwrapperref = React.createRef();
        this.currentnodeId = 0;
        this.flowindextosave = -1;
    }

    /**
     * Generate unique Id for node
     * @returns Increment and return
    */
    generateId = () : string => {
        return (++this.currentnodeId).toString();
    }

    /**
     * [COMPONENT LIFECYCLE]
     * Called before component is unmounted
     * 1. If flow data cannot be saved -> unmount without saving
     * 2. If flow data can be saved 
     *      a - Get the total number of elements (edge & node) in ALL tabs
     *      b - Set the number of total elements
     *      c - Loop through each flow and each element, and send the element data and its index (which flow it belongs ti) to server for saving
    */
    componentWillUnmount = () : void => {
        if (! this.props.flowdatacansave) {
            this.props.unmountwithoutsaving();
            return;
        }
        let totalelementscount = 0;
        for(let index = 0; index < this.state.dndelements.length; index ++) {
            totalelementscount += this.state.dndelements[index].length;
        }
        this.props.setnumberofelementstosend(totalelementscount);
        for(let index = 0; index < this.state.dndelements.length; index++) {
            let dndflow = this.state.dndelements[index];
            for (let count = 0; count < dndflow.length; count++) {
                let element = dndflow[count];
                if (isNode(element))
                    this.props.sendnodedataforsaving(index, element);
                else
                    this.props.sendedgedataforsaving(index, element);
            }   
        }
    }

    /**
     * remove all elements in the current tab
     */
    removeelementscurrenttab = () : void => {
        if (this.state.dndelements[this.state.activetab].length !== 0){
            this.setState (
                {
                    dndelements: 
                    [ 
                        ...this.state.dndelements.slice(0, this.state.activetab),
                        [],
                        ...this.state.dndelements.slice(this.state.activetab + 1)
                    ]
                }
            )
        }
    }

    restoreelementcurrenttab = (element: FlowElement) : void => {
        if (this.state.activetab >= this.state.dndelements.length) {
            let numbertoadd = this.state.activetab - this.state.dndelements.length + 1;
            for (let counter = 0; counter < numbertoadd; counter++) {
                this.setState({dndelements: [ ...this.state.dndelements, [] ]}, () => this.addelement(this.state.activetab, element));
            }
        } else {
            this.addelement(this.state.activetab, element);
        }
    }

    /**
     * Restore previously saved element
     * 1. If target tabindex is greater than number of current tab, add new tab(s) before adding the element
     * @param tabindex - Tab index which element belongs to 
     * @param element - node or edge
    */
    restoreelement = (tabindex: number, element: FlowElement) : void => {
        if (tabindex >= this.state.dndelements.length) {
            let numbertoadd = tabindex - this.state.dndelements.length + 1;
            for (let counter = 0; counter < numbertoadd; counter++) {
                this.setState({dndelements: [ ...this.state.dndelements, [] ]}, () => this.addelement(tabindex, element));
            }
        } else {
            this.addelement(tabindex, element);
        }
    }
    

    /**
     * Add element to target flow
     * 1. Update value of currentnodeId to max(node.id, currentnodeId) -> Ensure all nodes' id will be unique
     * 2. If element is a node, reset node.data.error to BOOLEAN (previously STRING)
     * @param tabindex - Tab index which element belongs to 
     * @param element - FlowElement object (node or edge)
    */
    addelement = (tabindex: number, element: FlowElement) : void => {
        if(element === void 0) {
            alert("element is the special value `undefined`");
            return;
          }
        if (isNode(element)) {
            this.currentnodeId = Math.max(Number(element.id), this.currentnodeId);
            element.data.error = false;
        }
        this.setState (
            {
                dndelements: 
                [ 
                    ...this.state.dndelements.slice(0, tabindex),
                    [ ...this.state.dndelements[tabindex], element ],
                    ...this.state.dndelements.slice(tabindex + 1)
                ]
            }
        )
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
                dndelements:
                [
                    ...this.state.dndelements.slice(0, this.state.activetab),
                    addEdge({...params, type: "arrowclosed", style: { strokeWidth:3, stroke:"grey" }, arrowHeadType: ArrowHeadType.ArrowClosed}, this.filterInvalidEdges(params)),
                    ...this.state.dndelements.slice(this.state.activetab + 1)
                ]
            }
        )
    }

    /**
     * Loop through all edges (in active tab) and filter out invalid edge(s)
     * @param newedge - New edge
     * @returns - Elements (array) which contains only valid edges (nodes will not be checked!)
    */
    filterInvalidEdges = (newedge: Edge<any> | Connection) : Elements => {
        return this.state.dndelements[this.state.activetab].filter((element) => isEdge(element)? this.edgeValidationChecker(element, newedge) : true);
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
    edgeValidationChecker = (existingedge : Edge<any> | Connection, newedge : Edge<any> | Connection) : boolean => {
        if (newedge.source === existingedge.source) return false;
        if (newedge.source === existingedge.target && newedge.target === existingedge.source) return false;
        return true;
    }

    /**
     * [REACTFLOW CALLBACK]
     * Called when element(s) is/are removed from flow
     * 1. Remove element from dndelements (state)
     * @param elements 
    */
    onElementsRemove = (elements: Elements) : void => {
        this.setState(
            {
                dndelements:
                [
                    ...this.state.dndelements.slice(0, this.state.activetab),
                    removeElements(elements, this.state.dndelements[this.state.activetab]),
                    ...this.state.dndelements.slice(this.state.activetab + 1)
                ]
            }
        )
    }

    /**
     * [REACTFLOW CALLBACK]
     * Called when reactflow is loaded
     * 1. Update reactflow instance
     * @param rfInstance 
    */
    onLoad = (rfInstance: OnLoadParams) : void => {
        this.setState({ rfInstance });
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
        let reactFlowBounds = this.rfwrapperref.current.getBoundingClientRect();
        let type = event.dataTransfer.getData("application/reactflow");
        if (this.state.rfInstance !== null) {
            // TODO: find exact difference in position instead of hardcoding values
            // TODO: find the exact part of the node that the user is grabbing in menu bar and use that for precise corrections 
            let position = this.state.rfInstance.project({
                x: event.clientX - reactFlowBounds.left - 50,
                y: event.clientY - reactFlowBounds.top - 50,
            })

            let data = this.props.preparenodedata(type);
            data['error'] = false;
            let newnode = {id: this.generateId(), type, position, data};
            this.setState(
                { 
                    dndelements:
                    [
                        ...this.state.dndelements.slice(0, this.state.activetab),
                        [ ...this.state.dndelements[this.state.activetab], newnode],
                        ...this.state.dndelements.slice(this.state.activetab + 1)
                    ]
                }
            );
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
        this.setState(
            {
                dndelements: 
                [
                    ...this.state.dndelements.slice(0, this.state.activetab),
                    [ 
                        ...this.state.dndelements[this.state.activetab].slice(0, nodeindex),
                        { ...this.state.dndelements[this.state.activetab][nodeindex], position: node.position },
                        ...this.state.dndelements[this.state.activetab].slice(nodeindex + 1)
                    ],
                    ...this.state.dndelements.slice(this.state.activetab + 1)
                ]
            }
        )
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
     * Find the index of node in active tab elements using its nodeId
     * @param nodeid - Id of node
     * @returns - Index of node in dndelements[activetab], -1 if not found
    */
    findNodeIndex = (nodeid: string) : number => {
        return this.state.dndelements[this.state.activetab].findIndex(node => node.id === nodeid);
    }

    /**
     * Find the first occurance of node type (Iterate through dndelements[activetab] array)
     * @param nodetype 
     * @returns Return the nodeId if found, else return null
    */
    searchFirstOccuranceOfNodeType = (nodetype: string) : string | null => {
        let activeElements = this.state.dndelements[this.state.activetab];
        for(let index = 0; index < activeElements.length; index++) {
            if (activeElements[index].type === nodetype)
                return activeElements[index].id;
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
    findConnectedNode = (nodeId: string) : FlowElement | null => {
        let activeElements = this.state.dndelements[this.state.activetab];
        for (let index = 0; index < activeElements.length; index ++) {
            let element = activeElements[index];
            if (isEdge(element) && element.source === nodeId)
                return this.getFlowElementById(element.target);
        }
        return null;
    }

    /**
     * Get the Element/Node object using Id
     * If id is not found, return null
     * @param nodeId 
    */
    getFlowElementById = (nodeId: string) : FlowElement | null => {
        let activeElements = this.state.dndelements[this.state.activetab];
        for (let index = 0; index < activeElements.length; index ++) {
            if (activeElements[index].id === nodeId)
                return activeElements[index];
        }
        return null;
    }

    /**
     * Set background of node with id = nodeId to red (error node)
     * 1. Find its index and update (node.data.error) to true
     * @param nodeId - Id of error node
     */
    setErrorNode = (nodeId: string) : void => {
        let nodeindex = this.findNodeIndex(nodeId);
        this.setState(
            {
                dndelements: 
                [
                    ...this.state.dndelements.slice(0, this.state.activetab),
                    [ 
                        ...this.state.dndelements[this.state.activetab].slice(0, nodeindex),
                        { 
                            ...this.state.dndelements[this.state.activetab][nodeindex],  
                            data: {...this.state.dndelements[this.state.activetab][nodeindex].data, error: true},
                        },
                        ...this.state.dndelements[this.state.activetab].slice(nodeindex + 1)
                    ],
                    ...this.state.dndelements.slice(this.state.activetab + 1)
                ]
            }
        )
    }

    /**
     * Set background of all node to default
     * 1. Reset (node.data.error) of ALL nodes to false
    */
    resetErrorNode = () : void => {
        let activeElements = this.state.dndelements[this.state.activetab];
        for (let index = 0; index < activeElements.length; index ++) {
            if (isNode(activeElements[index]))
                activeElements[index].data.error = false;
        }
        this.setState(
            { 
                dndelements:
                [
                    ...this.state.dndelements.slice(0, this.state.activetab),
                    activeElements,
                    ...this.state.dndelements.slice(this.state.activetab + 1)
                ]
            }
        )
    }

    /**
     * Update the data of nodeId in active tab
     * @param nodeId - Id of node
     * @param data - Latest data to update
    */
    updatenodedata = (nodeId: string, data: Dictionary) : void => {
        let nodeindex = this.findNodeIndex(nodeId);
        this.setState(
            {
                dndelements: 
                [
                    ...this.state.dndelements.slice(0, this.state.activetab),
                    [ 
                        ...this.state.dndelements[this.state.activetab].slice(0, nodeindex),
                        { ...this.state.dndelements[this.state.activetab][nodeindex], data },
                        ...this.state.dndelements[this.state.activetab].slice(nodeindex + 1)
                    ],
                    ...this.state.dndelements.slice(this.state.activetab + 1)
                ]
            }
        )
    }

    /**
     * 1. Append new dndflow tab
     * 2. Change active tab to new tab
    */
    addnewdndflow = () : void => {
        let currentcount = this.state.dndelements.length;
        this.setState({ dndelements: [ ...this.state.dndelements, []] }, () => this.changeactivetab(currentcount));
    }

    /**
     * Change the activetab 
     * @param activetab - Index of new active tab
    */
    changeactivetab = ( activetab: number) : void => {
        this.setState({ activetab });
    }

    /**
     * Called when LOAD button is clicked
     * Open load modal and let users fill the directory and filname
     * @param tabindex - Index of flow tab which users wants to load previous flow     
     */
    loadbtnOnClick = () : void => {
        this.openloadmodal();
    }

    /**
     * Called when SAVE button is clicked
     * 1. Update value of "flowindextosave" to tabindex
     * 2. Open save modal (let user to fill in the directory and filename)
     * @param tabindex - Index of flow tab which user wants to save
    */
    savebtnOnclick = (tabindex: number) : void => {
        this.flowindextosave = tabindex;
        this.opensavemodal();
    }

    /**
     * Called when DELETE button is clicked
     * 1. Remove the entire flow from dndelements
     * @param indextodelete - Index of flow tab which user wants to delete
    */
    deletedndflow = (indextodelete: number) : void => {
        this.setState({ dndelements: [ ...this.state.dndelements.slice(0, indextodelete), ...this.state.dndelements.slice(indextodelete + 1)]}, () => {
            if (this.state.activetab === indextodelete && indextodelete !== 0)
                this.changeactivetab(indextodelete - 1);
            else if (this.state.activetab !== indextodelete && indextodelete < this.state.activetab)
                this.changeactivetab(this.state.activetab - 1);
        })
    }

    /**
     * [UPDATE COMPONENT'S STATE] 
     * Set loadmodalisactive to true => Load modal (which allow user to set directory and filename) opens
    */
    openloadmodal = () : void => {
        this.setState({ loadmodalisactive: true });
    }

    /**
     * [UPDATE COMPONENT'S STATE] 
     * Set loadmodalisactive to false => Load modal (which allow user to set directory and filename) closes
    */
    closeloadmodal = () : void => {
        this.setState({ loadmodalisactive: false });
    }


    /**
     * [UPDATE COMPONENT'S STATE] 
     * Set savemodalisactive to true => Save modal (which allow user to set directory and filename) opens
    */
    opensavemodal = () : void => {
        this.setState({ savemodalisactive: true });
    }

    /**
     * [UPDATE COMPONENT'S STATE] 
     * Set savemodalisactive to false => Save modal (which allow user to set directory and filename) closes
    */
    closesavemodal = () : void => {
        this.setState({ savemodalisactive: false });
    }

    /**
     * Called when user clicked on LOAD button in load modal
     * 
     * 
     * @param directory - Directory to load JSON file
     * @param filename - Filename of JSON file
    */
    loadflow = (directory: string, filename: string) : void => {
        this.props.manualloadflow(directory, filename);
    }

    /**
     * Called when user clicked on SAVE button in save modal
     * 1. Get all the elements of the flow which user wants to save
     * 2. Invoke manualsaveflow callback function
     * @param directory - Directory to save JSON file
     * @param filename - Filename of JSON file
    */
    saveflow = (directory: string, filename: string) : void => {
        let elementstosave = this.state.dndelements[this.flowindextosave];
        this.props.manualsaveflow(elementstosave, directory, filename);
    }

    render = () => {
        return (
            <>
                <SaveFlowModal 
                    active={this.state.savemodalisactive}
                    saveflow={this.saveflow}
                    closemodal={this.closesavemodal}
                />
                <LoadFlowModel 
                    active={this.state.loadmodalisactive}
                    loadflow={this.loadflow}
                    closemodal={this.closeloadmodal}
                />
                <div ref={this.rfwrapperref} style={{ flexGrow:1, height: '100%' }}>
                    <div className='tabs'>
                        { this.state.dndelements.map ((elements, index) => 
                            <TabComponent
                                key={index}
                                tabisactive={this.state.activetab === index}
                                tabcandelete={this.state.dndelements.length > 1}
                                tabcansave={this.props.flowdatacansave}
                                tabcanload={this.props.flowdatacansave}
                                tabindex={index}
                                tabOnclick={this.changeactivetab}
                                savebtnOnclick={this.savebtnOnclick}
                                loadbtnOnclick={this.loadbtnOnClick}
                                detelebtnOnclick={this.deletedndflow}
                            />
                        ) }
                        <Button className='add-tab-btn' onClick={this.addnewdndflow}>+</Button>
                    </div>
                    <ReactFlow
                        elements={this.state.dndelements[this.state.activetab]}
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
            </>
        )
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * TabComponent props
 * tabisactive: If the tab is currently active
 * tabcandelete: If user can delete the tab (False if it is the last and only tab)
 * tabcansave: If user can save the flow (False if client is not connected to server)
 * tabcanload: If the user can load a flow (False if client is not connected to server)
 * tabindex: Index of the tab
 * tabOnclick: Callback function when user clicked on the tab
 * savebtnOnclick: Callback function when user clicked on SAVE button of the tab
 * loadbtnOnclick: Callback function when user clicks on LOAD button of the tab
 * deletebtnOnclick: Callback function when user clicked on DELETE button on the tab
*/
interface TabComponentProps {
    tabisactive: boolean;
    tabcandelete: boolean;
    tabcanload: boolean;
    tabcansave: boolean;
    tabindex: number;
    tabOnclick: ((tabindex: number) => void);
    loadbtnOnclick:(() => void);
    savebtnOnclick:((tabindex: number) => void);
    detelebtnOnclick: ((tabindex: number) => void);
}

/**
 * TanComponent states
 * menuvisible: If the menu is active (visible)
*/
interface TabComponentStates {
    menuvisible: boolean;
}

class TabComponent extends Component <TabComponentProps, TabComponentStates> {
    constructor(props: TabComponentProps) {
        super(props);
        this.state = { menuvisible: false }
    }

    /**
     * [UPDATE COMPONENT'S STATE] - menuvisible
     * @param menuvisible 
    */
    setmenuvisible = (menuvisible: boolean) : void => {
        this.setState({ menuvisible });
    }

    /**
     * Open tab menu when user right-clicked on the tab
     * @param event 
    */
    onContextMenu = (event: React.MouseEvent<HTMLElement>) : void => {
        event.preventDefault();
        this.setmenuvisible(true);
    }

    /**
     * Called when user clicks on LOAD button on the menu
     * @param event
     */
    loadbtnOnclick = (event: React.MouseEvent<HTMLElement>) : void => {
        event.stopPropagation();
        this.props.loadbtnOnclick();
    }

    /**
     * Called when user clicked on SAVE button on the menu
     * 1. Stop propagation -> Prevent changing of active tab
     * 2. Invoke savebtnOnclick callback function
     * @param event 
    */
    savebtnOnclick = (event: React.MouseEvent<HTMLElement>) : void => {
        event.stopPropagation();
        this.props.savebtnOnclick(this.props.tabindex);
    }

    /**
     * Called when user clicked on DELETE button on the menu
     * 1. Stop propagation -> Prevent changing of active tab
     * 2. Invoke detelebtnOnclick callback function
     * @param event 
    */
    deletebtnOnclick = (event: React.MouseEvent<HTMLElement>) : void => {
        event.stopPropagation();
        this.setmenuvisible(false);
        this.props.detelebtnOnclick(this.props.tabindex);
    }

    /**
     * Called when user clicked on CLOSE button on the menu
     * 1. Stop propagation -> Prevent changing of active tab
     * 2. Close the menu
     * @param event 
    */
    closebtnOnclick = (event: React.MouseEvent<HTMLElement>) : void => {
        event.stopPropagation();
        this.setmenuvisible(false);
    }


    render = () => {
        return (
            <div 
                className='tab-component'    
                style={{ backgroundColor: this.props.tabisactive? "white" : "grey" }} 
                onClick={() => this.props.tabOnclick(this.props.tabindex)}
                onContextMenu={this.onContextMenu}
            >
                FLOW {this.props.tabindex + 1}
                { this.state.menuvisible && 
                    <div className='tab-component-menu'>
                        <Button block onClick={this.loadbtnOnclick}disabled={! this.props.tabcanload}>Load</Button>
                        <Button block onClick={this.savebtnOnclick} disabled={! this.props.tabcansave}>Save</Button>
                        <Button block onClick={this.deletebtnOnclick} disabled={! this.props.tabcandelete}>Close</Button>
                        <Button block onClick={this.closebtnOnclick}>hide</Button>
                    </div>
                }
            </div>
        )
    }
}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * SaveFlowModal props
 * active: If modal is active (visible)
 * closemodal: Callback function when user clicks on CLOSE button
 * saveflow: Callback function when user clicks on SAVE button
*/
interface SaveFlowModalProps {
    active: boolean;
    closemodal: (() => void);
    saveflow: ((directory: string, filename: string) => void);
}

/**
 * SaveFlowModal states
 * directory - Directory to save/export the JSON file 
 * filename - Filename of the JSON file
*/
interface SaveFlowModalStates {
    directory: string;
    filename: string;
}

class SaveFlowModal extends Component <SaveFlowModalProps, SaveFlowModalStates> {

    constructor(props: SaveFlowModalProps) {
        super(props);
        this.state = { directory: "C://Users/User/Desktop", filename: "testflowmanualsave" }
    }

    /**
     * Called when directory (state) on change
     * 1. Update value of directory
     * @param event 
    */
    handledirectoryOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({ directory: event.target.value });
    }

    /**
     * Called when filename (state) on change
     * 1. Update value of filename
     * @param event 
    */
    handlefilenameOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({ filename: event.target.value });
    }

    /**
     * Called when user clicks on CLOSE button 
     * 1. Reset value of directory and filename (state) 
     * 2. Invoke closemodal props callback function
    */
    closebtnOnclick = (event: React.MouseEvent<HTMLElement>) : void => {
        event.stopPropagation();
        this.props.closemodal();
    }

    /**
     * Called when user clicks on SAVE button
     * 1. Invoke saveflow props callback function
     * 2. Reset value of diretory and filename (state)
     * 3. Invoke closemodal props callback function
    */
    savebtnOnclick = () : void => {
        this.props.saveflow(this.state.directory, this.state.filename);
        this.setState({ directory: "C://Users/User/Desktop", filename: "testflowmanualsave" });
        this.props.closemodal();
    }

    /**
     * Check if user is allowed to save a flow
     * False if value of directory or filename is blank
     * @returns True if value of directory or filename is not blank (!= )
    */
    checksavebtnavailable = () : boolean => {
        if (this.state.directory === "" || this.state.filename === "") return false;
        return true;
    }

    render = () => {
        return (
            <Modal centered isOpen={this.props.active}>
                <ModalHeader>CNNFlow Export (JSON)</ModalHeader>
                <ModalBody>
                    <Form>
                        <FormGroup>
                            <Label for='directory'>Directory</Label>
                            <Input type='text' name='directory' value={this.state.directory} onChange={this.handledirectoryOnchange} valid={this.state.directory !== ""} invalid={this.state.directory === ""}></Input>
                            <FormFeedback valid={false} >Directory cannot be blank</FormFeedback>
                        </FormGroup>
                        <FormGroup>
                            <Label for='filename'>Filename</Label>
                            <Input type='text' name='filename' value={this.state.filename} onChange={this.handlefilenameOnchange} valid={this.state.filename !== ""} invalid={this.state.filename === ""}></Input>
                            <FormFeedback valid={false} >Filename cannot be blank</FormFeedback>
                        </FormGroup>
                    </Form>
                </ModalBody>
                <ModalFooter>
                    <Button block onClick={this.savebtnOnclick} disabled={! this.checksavebtnavailable()}>SAVE</Button>
                    <Button block onClick={this.closebtnOnclick}>CLOSE</Button>
                </ModalFooter>
            </Modal>
        )
    }
}



/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

/**
 * Load k props
 * active: If modal is active (visible)
 * closemodal: Callback function when user clicks on CLOSE button
 * saveflow: Callback function when user clicks on SAVE button
*/
interface LoadModalProps {
    active: boolean;
    closemodal: (() => void);
    loadflow: ((directory: string, filename: string) => void);
}

/**
 * SaveFlowModal states
 * directory - Directory to save/export the JSON file 
 * filename - Filename of the JSON file
*/
interface FlowModalStates {
    directory: string;
    filename: string;
}

class LoadFlowModel extends Component <LoadModalProps, FlowModalStates> {

    constructor(props: LoadModalProps) {
        super(props);
        this.state = { directory: "C://Users/User/Desktop", filename: "testflowmanualload" }
    }

    /**
     * Called when directory (state) on change
     * 1. Update value of directory
     * @param event 
    */
    handledirectoryOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({ directory: event.target.value });
    }

    /**
     * Called when filename (state) on change
     * 1. Update value of filename
     * @param event 
    */
    handlefilenameOnchange = (event: React.ChangeEvent<HTMLInputElement>) : void => {
        this.setState({ filename: event.target.value });
    }

    /**
     * Called when user clicks on CLOSE button 
     * 1. Reset value of directory and filename (state) 
     * 2. Invoke closemodal props callback function
    */
    closebtnOnclick = () : void => {
        this.setState({ directory: "C://Users/User/Desktop", filename: "testflowmanualload" });
        this.props.closemodal();
    }

    /**
     * Called when user clicks on LOAD button
     * 1. Invoke saveflow props callback function
     * 2. Reset value of diretory and filename (state)
     * 3. Invoke closemodal props callback function
    */
    loadbtnOnclick = () : void => {
        this.props.loadflow(this.state.directory, this.state.filename);
        this.setState({ directory: "C://Users/User/Desktop", filename: "testflowmanualload" });
        this.props.closemodal();
    }

    /**
     * Check if user is allowed to load a flow
     * False if value of directory or filename is blank
     * @returns True if value of directory or filename is not blank (!= )
    */
    checkloadbtnavailable = () : boolean => {
        if (this.state.directory === "" || this.state.filename === "") return false;
        return true;
    }

    render = () => {
        return (
            <Modal centered isOpen={this.props.active}>
                <ModalHeader>CNNFlow Import (JSON)</ModalHeader>
                <ModalBody>
                    <Form>
                        <FormGroup>
                            <Label for='directory'>Directory</Label>
                            <Input type='text' name='directory' value={this.state.directory} onChange={this.handledirectoryOnchange} valid={this.state.directory !== ""} invalid={this.state.directory === ""}></Input>
                            <FormFeedback valid={false} >Directory cannot be blank</FormFeedback>
                        </FormGroup>
                        <FormGroup>
                            <Label for='filename'>Filename</Label>
                            <Input type='text' name='filename' value={this.state.filename} onChange={this.handlefilenameOnchange} valid={this.state.filename !== ""} invalid={this.state.filename === ""}></Input>
                            <FormFeedback valid={false} >Filename cannot be blank</FormFeedback>
                        </FormGroup>
                    </Form>
                </ModalBody>
                <ModalFooter>
                    <Button block onClick={this.loadbtnOnclick} disabled={! this.checkloadbtnavailable()}>LOAD</Button>
                    <Button block onClick={this.closebtnOnclick}>CLOSE</Button>
                </ModalFooter>
            </Modal>
        )
    }
}