namespace c_glib graphwalker.service
namespace java org.graphwalker.service
namespace cpp org.graphwalker.service
namespace rb graphwalker.service
namespace perl graphwalker.service
namespace csharp graphwalker.service
namespace js graphwalker.service
namespace st graphwalker.service
namespace py graphwalker.service
namespace py.twisted graphwalker.service
namespace go graphwalker.service
namespace php graphwalker.service
namespace delphi graphwalker.service
namespace cocoa graphwalker.service
namespace * org.graphwalker.service

//----------------------------------------------------------------------------------------------------------------------
// Types
//----------------------------------------------------------------------------------------------------------------------
typedef string UUID                                  // Universally unique identifier
typedef i64 Timestamp                                // milliseconds since January 1, 1970, 00:00:00 GMT
typedef string XML                                   //
typedef string Hash                                  // MD5 checksum
typedef string Token                                 // security/execution token
typedef string Step                                  //

//----------------------------------------------------------------------------------------------------------------------
// Authentication
//----------------------------------------------------------------------------------------------------------------------

enum Role {                                          //
    VIEW,                                            // see results
    NORMAL,                                          // + create and execute models
    ADMIN,                                           // + create users
}

struct User {                                        //
    1: required UUID uuid;                           // Unique id per user that never changes, set by the server
    2: required string username;                     //
    3: required string email;                        //
    4: required Timestamp created;                   // set by the server
    5: required Timestamp updated;                   // set by the server
    6: required Timestamp deleted;                   // set by the server
    7: required bool active;                         //
    8: required Role role;                           //
}

//----------------------------------------------------------------------------------------------------------------------
// Model
//----------------------------------------------------------------------------------------------------------------------

struct Model {                                       // represent a model in the comunication between server and client
    1: optional UUID uuid;                           // Unique id for the model, set by the server
    2: required string name;                         // the name of the model
    3: required XML content;                         // the model it self
    4: optional Hash contentHash;                    // set by the server
    5: optional Timestamp created;                   // set by the server
    6: optional Timestamp updated;                   // set by the server
    7: optional Timestamp deleted;                   // set by the server
    8: optional i32 revision;                        // if the model is updated, this is incremented. set by the server
}

//----------------------------------------------------------------------------------------------------------------------
// Execution
//----------------------------------------------------------------------------------------------------------------------

enum PathGeneratorType {                             //
    RANDOM_LEAST_VISITED,                            //
    RANDOM,                                          //
    RANDOM_UNVISITED_FIRST,                          //
}

struct PathGenerator {                               //
    1: required PathGeneratorType type;              //
}

enum StopConditionType {                             //
    EDGE_COVERAGE,                                   //
    LENGTH,                                          //
    NEVER,                                           //
    REACHED_EDGE,                                    //
    REACHED_VERTEX,                                  //
    REQUIREMENT_COVERAGE,                            //
    TIME_DURATION,                                   //
    VERTEX_COVERAGE,                                 //
}

struct StopCondition {                               //
    1: required StopConditionType type;              //
    2: optional string value;                        //
}

enum Status {                                        //
    EXECUTING,                                       //
    COMPLETED,                                       //
    FAILED,                                          //
}

enum Device {                                        // Classify the execution
    HANDHELD,                                        //
    PC,                                              //
    TABLET,                                          //
}

struct ExecutionContext {                            //
    1: optional UUID uuid;                           // Unique id for the execution
    2: required list<Model> models;                  //
    3: optional Step currentStep;                    //
    4: required PathGenerator pathGenerator;         //
    5: required StopCondition stopCondition;         //
    6: optional Status status;                       //
    7: optional Timestamp started;                   //
    8: optional i32 duration;                        //
    9: optional double fulfillment;                  //
   10: optional Device device;                       //
}

//----------------------------------------------------------------------------------------------------------------------
// Result
//----------------------------------------------------------------------------------------------------------------------

struct Result {                                      // result from a model execution
    1: required UUID uuid;                           //
    2: required Model model;                         //
    3: required Timestamp started;                   //
    4: required i32 duration;                        //
    5: required PathGenerator pathGenerator;         //
    6: required StopCondition stopCondition;         //
    7: required i32 edgeCoverage;                    //
    8: required i32 vertexCoverage;                  //
    9: required i32 requirementCoverage;             //
   10: required Status status;                       //
}

struct ResultList {                                  //
    1: required map<Model, list<Result>> results;    //
}

struct ResultFilter {                                //
    1: optional list<Model> models;                  //
    2: optional Timestamp after;                     //
    3: optional Timestamp before;                    //
}

//----------------------------------------------------------------------------------------------------------------------
// Service
//----------------------------------------------------------------------------------------------------------------------
service GraphWalkerService {

    //------------------------------------------------------------------------------------------------------------------
    // Authentication
    //------------------------------------------------------------------------------------------------------------------
    Token authenticate(1:string username, 2:string password),

    //------------------------------------------------------------------------------------------------------------------
    // Models
    //------------------------------------------------------------------------------------------------------------------
    list<Model> listModels(1:Token token),
    Model getModel(1:Token token, 2:Model model),    // Object 2:Model = Hibernate Example object
    Model createModel(1:Token token, 2:Model model),
    Model updateModel(1:Token token, 2:Model model),
    void deleteModel(1:Token token, 2:Model model),

    //------------------------------------------------------------------------------------------------------------------
    // Execution
    //------------------------------------------------------------------------------------------------------------------
    ExecutionContext execute(1:Token token, 2:ExecutionContext context),
    bool hasMoreSteps(1:Token token, 2:ExecutionContext context),
    ExecutionContext getNextStep(1:Token token, 2:ExecutionContext context),
    ExecutionContext fail(1:Token token, 2:ExecutionContext context),

    //------------------------------------------------------------------------------------------------------------------
    // Result
    //------------------------------------------------------------------------------------------------------------------
    ResultList findResults(1:Token token, 2:ResultFilter filter),

    //------------------------------------------------------------------------------------------------------------------
    // Result
    //------------------------------------------------------------------------------------------------------------------
    list<ExecutionContext> getCurrentExecutionContexts(1:Token token),

}
