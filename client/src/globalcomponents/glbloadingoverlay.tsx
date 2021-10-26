import React, {Component} from 'react'
import {Spinner} from 'reactstrap'
import "./glbloadingoverlay.css"

/**
 * Loading screen overlay props
 * active: If loading screen overlay is active (visible) 
*/
interface LSOProps {
    active: boolean;
}

/**
 * Loading screen overlay states
*/
interface LSOStates {

}

export default class LoadingScreenOverlay extends Component <LSOProps, LSOStates>{
    constructor(props: LSOProps) {
        super(props);
        this.state = {
            active: false,
        }
    }

    render = () => {
        return (
            <>
                { this.props.active && 
                    <div className='OverlayBackground'> 
                        <Spinner  type='grow' color='dark'></Spinner>
                    </div>
                }
            </>
        )
    }
}