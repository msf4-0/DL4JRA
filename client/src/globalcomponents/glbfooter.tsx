import { Component } from 'react'
import {withRouter} from 'react-router'
import "./glbfooter.css"

class FooterComponent extends Component <any, any>{
    render = () => {
        return (
            <footer>
                <div>
                    @2021 COPYRIGHT
                </div>
            </footer>
        )
    }
}

export default withRouter(FooterComponent);