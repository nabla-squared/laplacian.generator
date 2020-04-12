[![CircleCI](https://circleci.com/gh/nabla-squared/laplacian.generator.svg?style=shield)](https://circleci.com/gh/nabla-squared/laplacian.generator)

Laplacian Generator
===========================
A tiny utility that generates files with arbitary directry tree structure using [Handlebars](http://jknack.github.io/handlebars.java/) templates.

Getting Started
----------------

In the following example, we will build a tiny tool that generates a html presentation from yaml data set.

### Prerequisites
JDK 8 or later is installed. (Tested on Zulu JDK 8)


### Creating a new generator project
Run the following command in a newly created directory(=project root).

```console
$ curl -Ls https://git.io/fhxcl | bash
```

This will create the following files and empty directories.

```console
$ tree
.
├── model/
├── template/
├── laplacian-module.yml
├── build.gradle.kts
├── settings.gradle.kts
├── gradlew
└── gradle/
    └── wrapper
        ├── gradle-wrapper.jar
        └── gradle-wrapper.properties
```

The **model** direcotry is where we place some yaml files applied to the **templates**, which reside in the **template** directory.

### Adding a model file
Firstly, add the following model file under the **model** directory.

```console
$ vim model/sample-presentation.yml
```

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

```console
$ tree model
model
└── sample-presentation.yml
````

### Creating a template

It is supposed that the presentation we are generating has the following directory structure.

```console
$ tree
.
└── presentation
    └── an-introduction-to-laplacian-generator
        ├── index.html
        └── pages
            ├── page-1.html
            └── page-2.html
```

So, firstly, we need to add the root directory of the presentation named "presentation" to the "template" direcotry.

```console
$ mkdir -p template/presentation

$ tree template
template
└── presentation
```

Then, run the following command at the project root to apply the template.

```console
$ ./gradlew lG
```

You will find a new directory added to the project root, which is copied from the template directory.


```console
$ tree
.
├── build.gradle.kts
├── settings.gradle.kts
├── model
│   └── sample-presentation.yml
│── template
│   └── presentation
└── presentation
````

### Using a markup in file path

Our **presentation** direcotry must contain a directory whose name is the title of the presentation replacing all the whitespaces to hyphen.

```console
$ tree presentation
presentation
└── an-introduction-to-laplacian-generator
```

Laplacian generator allows to use the subset of Handlebars markups in file path.
Any portions enclosed by curly braces ({...}) are evaluated by the Handlebars template engine while generating.

In this case, add the following directory.

```console
$ mkdir -p template/\{hyphen\ presentation.title\}

$ tree template
template
└── presentation
    └── {hyphen presentation.title}
```
`hyphen` is one of the custom helper functions, which replaces one or more consective whitespaces and punctuations to a hyphen "-"

### Adding a template file

Next, create the following template which generates the html containing the links to each pages.

If the extension of a template contains **".hbs."** or ends with **".hbs"**, its content is processed by the Handlebars template engine.

```console
$ vim template/presentation/\{hyphen\ presentation.title\}/index.html.hbs
```

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

```console
$ tree template
template
└── presentation
    └── {hyphen presentation.title}
        └── index.html.hbs
```

Run the command again to see the generated index html files.

```console
$ ./gradlew lG

$ tree presentation
presentation
└── an-introduction-to-laplacian-generator
    └── index.html
```

### Using the `each` helper

Next, to create a html file per each page, it is necessary to use the `each` helper.

```console
$ mkdir -p template/\{hyphen\ presentation.title\}/pages

$ vim template/presentation/\{hyphen\ presentation.title\}/pages/\{each\ presentation.pages\ as\ page\}page-\{@index\}.html.hbs
```

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
</html>
```

```console
$ tree template
template
└── presentation
    └── {hyphen presentation.title}
        ├── index.html.hbs
        └── pages
            └── {each presentation.pages as page}page-{@index}.html.hbs
```

Run the following command to see the result of the template.

```console
$ ./gradlew lG

$ tree presentation
presentation
└── an-introduction-to-laplacian-generator
    ├── index.html
    └── pages
        ├── page-1.html
        └── page-2.html
```
