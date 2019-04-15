Laplacian Generator
===========================
A small utility that generates arbitary directry tree structure and files using templates.

Getting Started
----------------

In the following example, we will build a tiny tool that generates a html presentation from yaml data set.

### Prerequisites
JDK 8 or later is installed. (Tested on Zulu JDK 8)


### Installing
Run the following command in a newly created directory.

```bash
bash <(curl -Ls https://git.io/fhxcl)
```

This will create the following files and empty directories.

```bash
(project root)
├── model/
├── template/
├── laplacian-module.yml
├── build.gradle.kts
└── settings.gradle.kts
```

The 'model' direcotry is where we place data files qpplied to the templates, which reside in the 'template' directory.

### Adding the model file
Firstly, add the following model file under the 'model' directory.

```bash
(project root)
├── build.gradle.kts
├── settings.gradle.kts
└── model
    └── sample-presentation.yml # ADD
````

**model/sample-presentation.yml**
```yaml
presentation:
  title: An introduction to Laplacian generator
  pages:
  - title: Feature summary
    content:
    - The first feature
    - Another feature
  - title: Working examples
    content:
    - The first step
    - The second step
```

### Creating the templates

It is supposed that the presentation we are generating has the following directory structure.

```bash
#(project root)
#└── presentation
#    └── an-introduction-to-laplacian-generator
#        ├── index.html
#        └── pages
#            ├── page-1.html
#            └── page-2.html
```
So, firstly, we need to add the root directory of the presentation named "presentation" to the "template" direcotry.

```bash
(project root)
└── template
    └── presentation #ADD
```

Then, run the following command at the project root directory to apply the template.

```bash
gradle lG
```

You will find a new directory added to the project root, which is copied from the template directory.

```bash
(project root)
├── build.gradle.kts
├── settings.gradle.kts
├── model
│   └── sample-presentation.yml
│── template
│   └── presentation
└── presentation #GENERATED
````

### Using markups in file paths

The "presentation" direcotry contains a directory whose name is the title of the presentation replacing  all the whitespaces to hyphen.

```bash
#.
#└── presentation
#    └── an-introduction-to-laplacian-generator
```

Laplacian generator allows to use the subset of Handlebars markups in file path.
Any portions enclosed by curly braces ({...}) are evaluated by the Handlebars template engine while generating.

In this case, add the following directory including

```bash
.
└── template
    └── presentation
        └── {hyphen presentation.title} # ADD
```
"hyphen" is one of the custom helper functions which replaces one or more consective whitespaces and punctuations to a hyphen "-"

### Adding a template file

Next, create the following template which generates the html containing the links to each pages.

```bash
└── template
    └── presentation
        └── {hyphen presentation.title}
            └── index.html.hbs # ADD
```

If the extension of a template contains ".hbs." or ends with ".hbs", its content are processed by the Handlebars template engine.

**template/presentation/{hyphen presentation.title}/index.html.hbs**
```html
<html>
  <h1>{{presentation.title}}</h1>
  <ul>
    {{#each pages in |page|}}
    <li>
      <a href="./pages/page-{{@index}}.html">
        {{page.title}}
      </a>
    </li>
    {{/each}}
  </ul>
</html
```

```bash
#    └── an-introduction-to-laplacian-generator
#        └── index.html
```

### Interating model


Next, to create a html file per each page, we need use the "each" helper.

```bash
└── template
    └── presentation
        └── {hyphen presentation.title}
            ├── index.html.hbs # ADD
            └── pages # ADD
                └── {each presentation.pages as page}page-{@index}.html.hbs # ADD
```

**template/presentation/{hyphen presentation.title}/index.html.hbs**
```html
<html>
  <h1>{{presentation.title}}</h1>
  <ul>
    {{#each pages in |page|}}
    <li>
      <a href="./pages/page-{{@index}}.html">
        {{page.title}}
      </a>
    </li>
    {{/each}}
  </ul>
</html
```


```bash
.
├── build.gradle.kts
├── settings.gradle.kts
├── model
│   └── sample-presentation.yml
└── template
    └── presentation #ADD
        └── {hyphen presentation.title} # ADD
            ├── index.html.hbs # ADD
            └── pages # ADD
                └── {each presentation.pages as page}page-{@index}.html.hbs # ADD
```
