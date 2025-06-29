This file contains instructions for AI agents working with the `integration` module.

## Testing

Before submitting any changes related to this module, ensure all tests pass by running the following command. Note that `/app` is assumed to be the repository root directory based on build logs.

`RUN cd /app && ./gradlew :integration:clean :integration:test`

This helps catch issues locally that might otherwise only appear in the CI pipeline. If these tests fail due to Docker or Java environment issues (e.g., `DockerClientProviderStrategy` errors or missing Java executables), those underlying environment problems need to be addressed outside of direct code changes.
