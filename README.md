Laplacian Generator
===========================

### Customizing the generate tasks

You can customize the behavior of `laplasianGenerate` task through
the extension interface as follows:

```kotlin
import laplacian.gradle.task.LaplacianGenerateExtension

configure<LaplacianGenerateExtension> {
    model {
        files("additional/model")
    }
    template {
        from("additional/template")
        into("targetDir")
    }
}
```
The generator task is based on Gradle's copy task. So, many settings
of it are also applicable to the generator task.

### How to create a new module

At first, on the root directory of the newly created project folder, create the module definition file in the following format:

**laplacian-module.yml**:

```yaml
project:
  group: laplacian   # the name of your project
  type: template     # the type of module, any one of the following: "template", "model", "generator"
  name: module-base  # the name of this module
  version: "1.0.0"   # the version of this module
```

Then, run the following command in the terminal:

```bash
bash <(curl -Ls https://git.io/fhxcl)
````

