# NetBeans CakePHP3 Plugin (Dev)

This plugin provides support for CakePHP3.

## Downloads

- https://github.com/junichi11/cakephp3-netbeans/releases

## How To Install

- Download a nbm (e.g. org-netbeans-modules-php-cake3-0.0.1-dev-201408251540.nbm)
- Tools > Plugins > Downloaded > Add Plugins
- Select the nbm
- Click Install

## How To Enable

- Open the project properties dialog (Right-Click your project > properties)
- Frameworks > CakePHP3 > Check `Enabled`

## Use Your Custom Directory Structure

If you just installed Cake3 via Composer, you don't have to do anything.

- Open the project properties
- Set relative paths from your Source Directory to Path settings

#### Root

Use when your CakePHP app directory exists in your php project as a subdirectory.

e.g. set `app` to Root setting like the following case:
```
source directory
    ├── foo
    ├── bar
    └── app
            ├── README.md
            ├── bin
            ├── composer.json
            ├── composer.lock
            ├── config
            ├── index.php
            ├── logs
            ├── phpunit.xml.dist
            ├── plugins
            ├── src
            ├── tests
            ├── tmp
            ├── vendor
            └── webroot
```

#### The Others

The same as App settings of config/app.php

#### Templates, plugins, locales, e.t.c.

Support them using `.cake` file.

## Features

- Code completion
- Smart Go To
- Run Actions (Run Command)
- Template files(Controller, Table, Helper, e.t.c.)
- Support for a `.cake` file
- Resolve mime-types for a ctp extension and a `.cake` file

### Code Completion

**Component**
```php
// in your Controller e.g. SomeController or AppController
public $components = ['Foo', 'My.Bar', 'Bar' => ['className' => 'MyBar'] ];

public initialize() {
    $this->loadComponent('Foo');
    $this->laodComponent('Bar', ['className' => 'MyBar']);
}

// in your Controller
$this->[Ctrl+Space]
```

**Helper**
```php
// in your Controller e.g. SomeController or AppController
public $helpers = ['Foo', 'My.Bar', 'Bar' => ['className' => 'MyBar'] ];

// in your template file e.g. index.ctp
$this->[Ctrl+Space]
```

**Table**
```php
// in your Controller e.g. SomeController 
$this->loadModel('Users');

// in your Controller
$this->[Ctrl+Space]
```

Please add `@property` to your table class if you want to use like the following:

```php
// e.g. BookmarksTable
/**
 * @property \App\Model\Table\TagsTable $Tags 
 */
class BookmarksTable extends Table {
}

$this->Bookmarks->Tags->[Ctrl+Space]
```

**Method Parameters**
```php
// e.g. path/to/your/template/index.ctp
// file path completion
$this->Html->css('[Ctrl+Space]');

// constants
$this->Html->docType('[Ctrl+Space]');
```

**NOTE**
At first, add a semicolon(;) if you want to use code completions for parameters. Tips: You can add it like the following: `Ctrl+;`

### Smart Go To

This feature is not enabled by default. If you want to use it, please set the KeyMap to it.
(Tools > Options > Keymap > Search `CakePHP` > CakePHP3: Smart Go To > e.g. set `Ctrl + Shift + J`) 

Files related to the current editor are shown when you run this action.
e.g. If your current file is a Controller, template, table, entity, testcase, conponent and helper file(s) will be shown.

You can change a list to specific category's one like the following.
(`Ctrl` is a Ctrl or Command key)

- Controller(All) : `Ctrl + C` (`Ctrl + Shift + C`)
- Component : `Ctrl + P` (`Ctrl + Shift + P`)
- Table : `Ctrl + M` (`Ctrl + Shift + M`)
- Entity : `Ctrl + E` (`Ctrl + Shift + E`)
- Behavior : `Ctrl + B` (`Ctrl + Shift + B`)
- Templates : `Ctrl + V` (`Ctrl + Shift + V`)
- View Cell : `Ctrl + L` (`Ctrl + Shift + L`)
- Helper : `Ctrl + H` (`Ctrl + Shift + H`)
- Fixture : `Ctrl + F` (`Ctrl + Shift + F`)
- TestCase : `Ctrl + T`
- Config : `Ctrl + I` or `Ctrl + Shift + I`

**NOTE** Core files are not shown (e.g. HtmlHelper, AuthComponent, e.t.c.)

### Support for a .cake file

You can use the [.cake](https://github.com/dotcake/dotcake) if you want to use the specified directories for Controller, Table, Template, e.t.c..
The file format is the following:
```json
{
    "cake": ".\/vendor\/cakephp\/cakephp",
    "build_path": {
        "entities": [
            ".\/src\/Model\/Entity\/"
        ],
        "tables": [
            ".\/src\/Model\/Table\/"
        ],
        "behaviors": [
            ".\/src\/Model\/Behavior\/"
        ],
        "controllers": [
            ".\/src\/Controller\/"
        ],
        "components": [
            ".\/src\/Controller\/Component\/"
        ],
        "templates": [
            ".\/src\/Template\/"
        ],
        "views": [
            ".\/src\/View\/"
        ],
        "helpers": [
            ".\/src\/View\/Helper\/"
        ],
        "consoles": [
            ".\/src\/Console\/"
        ],
        "shells": [
            ".\/src\/Shell\/"
        ],
        "tasks": [
            ".\/src\/Shell\/Task\/"
        ],
        "locales": [
            ".\/src\/Locale\/"
        ],
        "vendors": [
            ".\/vendor\/"
        ],
        "plugins": [
            ".\/plugins\/"
        ]
    }
}
```

**NOTE** It is not available in any categories.

## Actions

Right-click a project > CakePHP3

- Run Command
- Run server
- Refresh

### Run Command

All commands will be shown as a list in the command dialog. Then you can run a command with some parameters.

### Run server

Just run `cake server`. If you want to set details, please use `Run Configuration` of project properties.

### Refresh

Please run this action after you changed the `.cake` file or you updated the version of CakePHP.
Refresh the version number and category paths.

## License
[Common Development and Distribution License (CDDL) v1.0 and GNU General Public License (GPL) v2](http://netbeans.org/cddl-gplv2.html)
