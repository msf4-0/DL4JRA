import {BrowserRouter as Router, Route, Switch} from 'react-router-dom'

import HeaderComponent from "./globalcomponents/glbheader"
import FooterComponent from './globalcomponents/glbfooter';
import Mainpage from "./mainpage/mainpage"
import CNNSingletab from './cnndnd/cnnsingletab';
import CNNMultitab from "./cnndnd/cnnmultitab";
import ObjectDetection from './objectdetection/OD';
import Classification from "./classification/classification"
import DatasetGenerator from './datasetgeneration/dstgenerator';
import MQTTSignalling from './testing/mqttsignalling';

function App() {
  return (
    <div className="App">
      <Router>
          <HeaderComponent />
          <Switch>
              <Route path="/" exact component={Mainpage}></Route>
              <Route path="/cnnsingletab" exact component={CNNSingletab}></Route>
              <Route path="/cnnmultitab" exact component={CNNMultitab}></Route>
              <Route path="/odpretrained" exact component={ObjectDetection}></Route>
              <Route path='/datasetgenerator' exact component={DatasetGenerator}></Route>
              <Route path='/classification' exact component={Classification}></Route>
              <Route path='/sendmqttsignal' exact component={MQTTSignalling}></Route>
          </Switch>
          <FooterComponent />
      </Router>
    </div>
  );
}

export default App;
