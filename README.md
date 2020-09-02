# Large File Uploads

This application exposes a controller endpoint to send a file to, which is written back out to a temporary file.

## Running

Start the application with a restricted heap size of 256 or 512 megabytes. There is a run configuration saved in `.idea/runConfigurations`.

Run the following command to generate a file the tests expect to be there.
```shell script
dd if=/dev/zero of=$HOME/file.txt count=9 bs=1073741824
```

Then the application can be curled (change the home directory to your own).
```shell script
curl --location --request POST 'localhost:8080/upload' \
  --form 'file=@/Users/jamespace/file.txt'
```

## Expected Behavior
The file upload is streamed in small chunks, so the JVM Heap isn't overwhelmed.

## Actual Behavior
The JVM Heap will quickly fill up and the application will spend all its cpu time just doing garbage collection.
