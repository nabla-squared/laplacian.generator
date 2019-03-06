How to create a new module
===========================

At first, on the root directory of the newly created project folder, create the module definition file in the following format:

**laplacian-module.yml**:

```yaml
group: laplacian   # the name of your project
type: template     # the type of module, any one of the following: "template", "model", "generator"
name: module-base  # the name of this module
version: "1.0.0"   # the version of this module
```

Then, run the following command in the terminal:

```bash
bash <(curl -Ls https://git.io/fhxcl)
````

