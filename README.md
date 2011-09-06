# GraphWalker

GraphWalker is a tool for generating offline and online test sequences from Finite State Machines and
Extended Finite State Machines.
See also: http://graphwalker.org

The main features of GraphWalker are:

### No UML ###

GraphWalker's own ruleset in conjunction with GraphML, is easier to get started with than UML. As testers, we do not need all functionality that UML has to offer.

###No exit/stop points ###

The idea behind this, is that we want long, unpredictable test sequences. We do not want to walk the same path every time we execute a test. We want variation, spiced with randomness. This will create a better 'test coverage' of the system under test.
The way to tell GraphWalker to stop generating test sequences are done by means of Stop Criterias, passed as arguments to the tool.

### Online ###

GraphWalker supports online test sequence generation. Using the online mode, the tool is capable of  testing non-deterministic systems. In essence, this means that the path walked through the model is decided runtime, during the actual test execution. This is very helpful if your test execution tool needs to communicate with the model during the test.

### Event-driven ###

GraphWalker supports the possibility to switch model caused by an event. For example, let's say we have a model that executes the navigation of the GUI of a mobile phone. At any point in that execution, an incoming call will be such an event that will switch from navigating the GUI, to a model that handles the call.

## Reporting Issues

https://github.com/KristianKarl/GraphWalker/issues

## Build

mvn assembly:assembly

## Test

mvn test

## Examples and Help

http://graphwalker.org/documentation/

## License

http://graphwalker.org/license/