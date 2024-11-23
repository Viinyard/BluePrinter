![vtp-modified](https://github.com/user-attachments/assets/dfa9bbb9-f996-468b-80f8-92119db0ba39)

---
*Discover the power of BluePrinter, a template engine based on Apache Velocity designed to create files from templates.*


![GitHub Release](https://img.shields.io/github/v/release/Viinyard/blueprinter?include_prereleases&display_name=release&style=for-the-badge&label=Latest%20release)
![GitHub commits since latest release](https://img.shields.io/github/commits-since/Viinyard/blueprinter/latest?include_prereleases&style=for-the-badge)
![GitHub Repo stars](https://img.shields.io/github/stars/Viinyard/blueprinter?style=for-the-badge)
![GitHub forks](https://img.shields.io/github/forks/Viinyard/blueprinter?style=for-the-badge)
![GitHub watchers](https://img.shields.io/github/watchers/Viinyard/blueprinter?style=for-the-badge)
![GitHub top language](https://img.shields.io/github/languages/top/Viinyard/blueprinter?style=for-the-badge)

---

# Contents

- [Introduction](#introduction)
- [Why?](#why)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Features](#features)
- [Examples](#examples)
- [Contributing](#contributing)
- [License](#license)

## Introduction[![](https://raw.githubusercontent.com/aregtech/areg-sdk/master/docs/img/pin.svg)](#introduction)

**BluePrinter** is a template engine based on Apache Velocity designed to create files from templates.

It allows users to enter values for placeholders in the template and generate output files. BluePrinter can be used to automate file creation tasks and generate files with dynamic content.

Users can interact with BluePrinter through an interactive command-line interface that prompts them for input values. BluePrinter can be extended with plugins to add custom functionality and support additional features.


## Why?[![](https://raw.githubusercontent.com/aregtech/areg-sdk/master/docs/img/pin.svg)](#why)

I wanted a tool that allows me to:

- Create files from templates with placeholders.
- Prompt users for input values during file creation.
- Extend functionality with plugins, for exemple:
   - Fetch data from external sources. (API, Database, Environment Variables, Web Scraping, etc.) to populate templates.
   - Generate files in different formats. (JSON, XML, CSV, etc.)
   - Generate README files, with result of tests, benchmarks, etc.

You can find more examples of my use cases in my [adventofcode](https://github.com/Viinyard/adventofcode) repository.

## Prerequisites

- Java 21 or higher
- Maven 3.6.3 or higher

## Getting Started

To get started with BluePrinter, you can follow the installation instructions and use the command-line interface to create files from templates.

Or you can use the BluePrinter API to integrate it with your applications and extend its functionality with plugins.

## Installation

### Method 1: Install From Release
<details><summary><b>Show instructions</b></summary>

1. Download the latest release from the [releases page](https://github.com/Viinyard/blueprinter/releases/latest).
   - You can download the `blueprinter.zip` file for Windows or the `blueprinter.tar.gz` file for Linux.
2. Extract the contents of the archive in a `blueprinter` directory.
3. Add the `blueprinter` directory to your PATH environment variable.
   - On Windows, you can add the directory to the PATH using the System Properties dialog.
   - On Linux, you can add the directory to the PATH by editing the `.bashrc` or `.bash_profile` file.
     - Example: `export PATH=$PATH:/path/to/blueprinter`
4. Run the `bp` command to run BluePrinter.

</details>

### Method 2: Install From Source
<details><summary><b>Show instructions</b></summary>

1. Clone the repository:
    ```sh
    git clone https://github.com/yourusername/blueprinter.git
    cd blueprinter
    ```

2. Build the project using Maven:
    ```sh
    mvn clean install
    ```

3. Run the application:
    ```sh
    java -jar blueprinter-shell/target/blueprinter-shell-1.1.5.jar
    ```

</details>

### Method 3: As a Maven Dependency
<details><summary><b>Show instructions</b></summary>

1. Add the BluePrinter dependency to your `pom.xml` file:
    ```xml
    <dependency>
        <groupId>dev.vinyard.bp.runner</groupId>
        <artifactId>blue-printer-runner</artifactId>
        <version>1.2.0</version>
    </dependency>
    ```

</details>

## Usage

To use BluePrinter, you can run the application and follow the prompts to create files from templates. You can also extend its functionality by adding plugins.

### Command Line Interface

You can use the command line interface to interact with BluePrinter:

```sh
    java -jar blueprinter-1.0.0.jar --template my-template.vm --output my-output.txt
```

# Features
- Template Engine: Create files from templates using Apache Velocity.
- Interactive Prompts: Ask users for necessary information during file creation.
- Plugin Support: Extend functionality with custom plugins.
- Flexible Configuration: Configure templates and output files easily.

# Examples

## Creating a File from a Template

1. Create a template file `my-template.vm`:

    ```
    Hello, $name!
    ```
2. Run BluePrinter with the template:

    ```sh
    java -jar blueprinter-1.0.0.jar --template my-template.vm --output my-output.txt
    ```
   
3. Follow the prompts to enter the value for `name`.
4. The output file `my-output.txt` will be created with the content `Hello, <name>`.

## Using Plugins

1. Add your plugin dependency in `pom.xml`:

    ```xml
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>my-plugin</artifactId>
        <version>1.0.0</version>
    </dependency>
    ```
   
2. Implement your plugin and register it with BluePrinter.

# Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

1. Fork the repository
2. Create a new branch (`git checkout -b feature-branch`)
3. Make your changes
4. Commit your changes (`git commit -am 'Add new feature'`)
5. Push to the branch (`git push origin feature-branch`)
6. Create a new Pull Request

# License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.