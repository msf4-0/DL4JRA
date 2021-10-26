import React, {Component} from 'react'
import {Link} from 'react-router-dom'
import {withRouter} from 'react-router'
import "./glbheader.css"

class HeaderComponent extends Component <any, any> {
    render = () => {
        return (
            <header>
                <ul>
                    <li><Link className='nav-link' to="/">DL4JRA</Link></li>
                </ul>
            </header>
        )
    }
}
export default withRouter(HeaderComponent);