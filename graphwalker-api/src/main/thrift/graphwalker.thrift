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
typedef i32 UserID                                   //
typedef string Guid                                  // Globally unique identifier
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
    1: required UserID id;                           // Unique id per user that never changes
    2: required string username;                     //
    3: required string email;                        //
    4: required Timestamp created;                   //
    5: required Timestamp updated;                   //
    6: required Timestamp deleted;                   //
    7: required bool active;                         //
    8: required Role role;                           //
}

//----------------------------------------------------------------------------------------------------------------------
// Model
//----------------------------------------------------------------------------------------------------------------------

struct Model {                                       // represent a model in the comunication between server and client
    1: optional Guid id;                             // id for the model, set by the server
    2: required string name;                         // the name of the model
    3: required XML content;                         // the model it self
    4: optional Hash contentHash;                    // set by the server
    5: optional Timestamp created;                   // set by the server
    6: optional Timestamp updated;                   // set by the server
    7: optional Timestamp deleted;                   // set by the server
    8: optional i32 revision;                        // set by the server
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

enum Device {                                        //
    HANDHELD,                                        //
    PC,                                              //
    TABLET,                                          //
}

struct ExecutionContext {                            //
    1: required Guid id;                             // model id
    2: optional Step currentStep;                    //
    3: required PathGenerator pathGenerator;         //
    4: required StopCondition stopCondition;         //
    5: optional Status status;                       //
    6: optional Timestamp started;                   //
    7: optional Device device;                       //
}

//----------------------------------------------------------------------------------------------------------------------
// Result
//----------------------------------------------------------------------------------------------------------------------

struct Result {                                      // result from a model execution
    1: required Guid id;                             //
    2: required Timestamp started;                   //
    3: required i32 duration;                        //
    4: required PathGenerator pathGenerator;         //
    5: required StopCondition stopCondition;         //
    6: required i32 edgeCoverage;                    //
    7: required i32 vertexCoverage;                  //
    8: required i32 requirementCoverage;             //
    9: required Status status;                       //
}

struct ResultList {                                  //
    1: required map<Guid, list<Result>> results;     //
}

struct ResultFilter {                                //
    1: optional list<Guid> models;                   //
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
    Model getModel(1:Token token, 2:Guid id),
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

}
