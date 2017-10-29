We love pull requests from everyone.  By participating in this project, you agree to abide by the Code of Conduct, specified in the CODE_OF_CONDUCT.md file.

The best way to develop with Adrestia is to use the dependency [Docker Compose](https://docs.docker.com/compose/) file.  
This, combined with Gradle's handling of Java dependencies, will ensure that you spend absolutely no time setting up your
environment, and can get right into development.  To do this, you will need to have both [Docker](https://docs.docker.com/engine/installation/) and [Docker Compose](https://docs.docker.com/compose/install/) installed.

Start by forking the repository, cloning your repository, and changing to the main folder.

Then, change to the 'scripts/deps' folder and run:

`docker-compose up`

Now, you can execute the tests and other validations from the main folder of the repository:

`gradle clean check`

And start the application:

`gradle bootRun`

Push to your fork and submit a pull request.

At this point you're waiting on us. We like to at least comment on pull requests within three business days (and, typically, one business day). We may suggest some changes or improvements or alternatives.

Some things that will increase the chance that your pull request is accepted:

* Write tests
* Comment your code
* Write a good commit message
* Follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
* Several automated tools, including a linter, are run as part of the build process.  
* Be sure to fix any warnings thrown during the build process.
