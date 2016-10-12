# red-pencil

[Red Pencil kata](https://stefanroock.wordpress.com/2011/03/04/red-pencil-code-kata/)
by [Stefan Roock](https://stefanroock.wordpress.com/)

## How to run the tests

The project uses [Midje](https://github.com/marick/Midje/).

`lein midje` will run all tests.

`lein midje namespace.*` will run only tests beginning with "namespace.".

`lein midje :autotest` will run all the tests indefinitely. It sets up a
watcher on the code files. If they change, only the relevant tests will be
run again.
