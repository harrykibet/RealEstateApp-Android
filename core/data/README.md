# core:data

The `data` module implements the repository interfaces defined in `core:domain`. it acts as the "Source of Truth" for the entire application, orchestrating data from multiple sources.

## Key Responsibilities

- **Repository Implementation**: Concrete implementations of `IPropertyRepository`, `IAuthRepository`, etc.
- **Data Orchestration**: Managing logic between local storage (Room, DataStore) and remote sources (AWS Amplify via `core:network`).
- **Mapping**: Converting infrastructure-specific entities into domain-level models.
    - **Remote Mappers**: Located in `mappers/remote`, these map network entities (shared by Firebase and AWS) to domain models.
    - **Local Mappers**: Located in `mappers/room`, these map local database entities to domain models.
    - **Auth Mappers**: Specialized mappers for user authentication entities.
- **Synchronization**: Logic for syncing local drafts or offline state with the remote server.

## Data Flow

Data typically flows from a `RemoteDataSource` or `LocalDataSource` into a `Repository`, where it is mapped to a `DomainModel` before being exposed to the `domain` or `feature` layers.


## Dependency Graph
![Module Graph](module_graph.png)

