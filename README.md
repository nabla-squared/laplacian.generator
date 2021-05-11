[![CircleCI](https://circleci.com/gh/nabla-squared/laplacian.generator.svg?style=shield)](https://circleci.com/gh/nabla-squared/laplacian.generator)

Laplacian Generator
===========================
A tiny utility that generates files with arbitary directry tree structure using [Handlebars](http://jknack.github.io/handlebars.java/) templates.

Installation
----------------

### Prerequisites
JDK 8 or later is installed. (Tested on Zulu JDK 8)

### Install Laplacian Generator

```console
curl -Ls https://github.com/nabla-squared/laplacian.generator/releases/download/v1.0.0/install.sh | bash
```

```console
export LAPLACIAN_HOME=$HOME/.laplacian/dist/laplacian-generator-cli-1.0.0
```

```console
export PATH=$LAPLACIAN_HOME/bin:$PATH
```

### Run laplacian cli tool

```console
$ laplacian

Usage: laplacian [OPTIONS] COMMAND [ARGS]...

Commands:
  init      Initialize a generator project.
  generate  Generates resources from model data applying template files.

```

Getting Started
----------------

In the following example, we will build a tiny tool that generates a html presentation from yaml data set.

### Creating a new generator project
Firstly, we need to create the following two directories named **model** and **template**.
In the **model** directory, we place model data files which is written in yaml format.
The model data will be applied to the **templates**, which reside in the **template** directory.

```console
$ mkdir model template
```

```console
$ tree
.
├── model/
└── template/
```


### Adding a model file
Add a model file under the **model** directory.

```console
$ vim model/presentation.yml
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
        - title: Appendix
          content:
              - Appendix 1
              - Appendix 2
              - Appendix 3
    copyright: © Laplacian. 2021 All rights reserved
```

```console
$ tree model
model
└── presentation.yml
````

### Creating templates

We will create a html presentation having the following directory structure:

```console
$ tree
.
└── presentation
    └── an-introduction-to-laplacian-generator
        ├── index.html
        └── pages
            ├── page-1.html
            ├── page-2.html
            └── page-3.html
```

So, at first, we need to add a presentation root directory named "presentation" to the "template" directory:

```console
$ mkdir -p template/presentation

$ tree template
template
└── presentation
```

Then, run the following command at the project root to apply the template.

```console
$ laplacian generate
```

After running this command, you will see a new directory which is copied from the template directory.

```console
$ tree
.
├── model
│   └── presentation.yml
│── template
│   └── presentation
└── dest
    └── presentation
````

### Using a markup in file path

Our **presentation** directory must contain a directory whose name is the title of the presentation replacing all whitespace to hyphen.

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
    {{#each presentation.pages as |page|}}
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
        └── index.html.hbs
```

Run the command again to see the generated index html files.

```console
$ laplacian generate

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
<h3>{{page.title}}</h3>
<ul>
    {{#each page.content as |item|}}
    <li>{{item}}</li>
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
$ laplacian generate

$ tree presentation
presentation
└── an-introduction-to-laplacian-generator
    ├── index.html
    └── pages
        ├── page-1.html
        └── page-2.html
```
