# Large File Uploads

This application exposes a controller endpoint to send a file to, which is written back out to a temporary file.

## Running

Start the application with a restricted heap size of 256 or 512 megabytes. There is a run configuration saved in `.idea/runConfigurations`.

Run the following command to generate test files to use.
```shell script
# Small file of 1MB
dd if=/dev/zero of=$HOME/small.txt count=1024 bs=1024

# Large file of 9GB
dd if=/dev/zero of=$HOME/large.txt count=9 bs=1073741824
```

Then the application can be curled (change the home directory to your own).
```shell script

# Good
curl --location --request POST 'localhost:8080/mixed-upload' \
  --form 'text=Hello'
  --form 'smallFile=@/Users/jamespace/small.txt'
  --form 'largeFile=@/Users/jamespace/large.txt'

# Bad
curl --location --request POST 'localhost:8080/mixed-upload' \
  --form 'text=Hello'
  --form 'largeFile=@/Users/jamespace/large.txt'
  --form 'smallFile=@/Users/jamespace/small.txt'

# Good
curl --location --request POST 'localhost:8080/stream-upload' \
  --form 'text=Hello'
  --form 'smallFile=@/Users/jamespace/small.txt'
  --form 'largeFile=@/Users/jamespace/large.txt'

# Bad
curl --location --request POST 'localhost:8080/stream-upload' \
  --form 'text=Hello'
  --form 'largeFile=@/Users/jamespace/large.txt'
  --form 'smallFile=@/Users/jamespace/small.txt'

# Bad (Will never work because the nullable value isn't being sent)
curl --location --request POST 'localhost:8080/stream-upload-nulls' \
  --form 'text=Hello'
  --form 'smallFile=@/Users/jamespace/small.txt'
  --form 'largeFile=@/Users/jamespace/large.txt'
```

## Expected Behavior
The file upload is streamed in small chunks, so the JVM Heap isn't overwhelmed.

## Actual Behavior
The JVM Heap will quickly fill up and the application will spend all its cpu time just doing garbage collection.
