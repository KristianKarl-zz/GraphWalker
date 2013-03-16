namespace c_glib graphwalker
namespace java org.graphwalker.api
namespace cpp org.graphwalker.api
namespace rb graphwalker
namespace perl graphwalker
namespace csharp graphwalker
namespace js graphwalker
namespace st graphwalker
namespace py graphwalker
namespace py.twisted graphwalker
namespace go graphwalker
namespace php graphwalker
namespace delphi graphwalker
namespace cocoa graphwalker
namespace * org.graphwalker.api

typedef i64 Timestamp

struct Model {
    1: string name;
    2: string content;
    3: Timestamp created;
    4: Timestamp updated;
}

struct PathGenerator {
    1: string name;
}

struct StopCondition {
    1: string name;
    2: string value;
}

struct Result {
    1: Timestamp executed;
    // ...
}

service GraphWalker {

    // Check if the service is running
    string ping(1:string message),

    // Handle models
    set<Model> getModels(),
    Model getModel(1:string name),
    string createModel(1:string name, 2:string content),
    void updateModel(1:string name, 2:string content),
    void deleteModel(1:string name),

    // Handle execution of models
    set<StopCondition> getStopConditions(),
    set<PathGenerator> getPathGenerator(),
    void execute(1:string name, 2:PathGenerator generator, 3:StopCondition condition),
    bool hasMoreSteps(),
    string getCurrentStep(),
    string getNextStep(),
    void fail(),

    // Handle result

}
