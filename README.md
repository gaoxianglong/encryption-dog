<div align=center><img src="https://github.com/gaoxianglong/encryption-dog/blob/master/resources/logo.png"/></div>

[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html) ![License](https://img.shields.io/badge/build-passing-brightgreen.svg) ![License](https://img.shields.io/badge/version-1.6.0--RELEASE-blue)
> Encryption program with high performance, high security and rich functionsm.<br/>
> Supports binding the same physical device for file encryption and decryption.<br/>

## Use of EncryptionDog
### install
```shell
git clone https://github.com/gaoxianglong/encryption-dog.git
mvn package
alias dog = 'java -Xms512m -Xmx512m -Xmn128m -jar dog-1.6.0.jar'
```
or
```shell
$ wget https://github.com/gaoxianglong/encryption-dog/releases/download/1.6.0-RELEASE/dog-1.6.0.jar
alias dog = 'java -Xms512m -Xmx512m -Xmn128m -jar dog-1.6.0.jar'
```
### use
```shell
$ dog -h
Welcome to
   ____                       __  _           ___
  / __/__  __________ _____  / /_(_)__  ___  / _ \___  ___ _
 / _// _ \/ __/ __/ // / _ \/ __/ / _ \/ _ \/ // / _ \/ _ `/
/___/_//_/\__/_/  \_, / .__/\__/_/\___/_//_/____/\___/\_, /
                 /___/_/                             /___/
	version: 1.6.0-RELEASE

Usage: encrypt-dog [-bcdehoV] -k [-k]... [-n=<name>] -s=<source file>
                   [-t=<storage path>]
  -b, --sub-directory     Automatically encrypt and decrypt files in
                            subdirectories,default to false.
  -c, --compress          Compression is not enabled by default,Turning on
                            compression will increase execution time.
  -d, --delete            The source file is not deleted after the default
                            operation.
  -e, --encrypt           The default is decryption mode.
  -h, --help              Show this help message and exit.
  -k, --secret-key        Both encrypt and decrypt require the same secret key
  -n, --set-name=<name>   Set the name of the target file.
  -o, --only-local        Encryption and decryption operations can only be
                            performed on the same physical device.Only Apple
                            Mac is supported
  -s, --source-file=<source file>
                          Target files that need to be encrypt and decrypt,
                            Wildcards are supported.
  -t, --target-path=<storage path>
                          Storage path after operation,The default is stored in
                            the user home directory.
  -V, --version           Print version information and exit.
Copyright(c) 2021 - 2031 gaoxianglong. All Rights Reserved.
```
### highest security
Files encrypted on computer a can only be decrypted on computer a.<br/>
Principle:
<div align=center><img src="https://github.com/gaoxianglong/encryption-dog/blob/master/resources/hs.png"/></div>

WARN:
> Deleting or damaging the random key will never complete decryption.<br/>
```shell
# command -o, --only-local
# encrypt
$ dog -deos /Users/jiushu/Desktop/a.mp4 -k
Enter value for --secret-key (Both encrypt and decrypt require the same secret key):
Enter the secret-key again:
Please wait...

[Encrypt file number]:1/1
[Source path]:/Users/jiushu/Desktop/a.mp4
[Estimated time]:00:00:06
[>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>] 100%
[Encrypt result]:success
[Time consuming]:00:00:05,[Before size]:107.22MB,[After size]:142.96MB
[Target path]:/Users/jiushu/Desktop/a.mp4.dog

>>> Operation complete <<<
[Total time]:00:00:05
[Results]:total files:1,successes:1,failures:0

# decrypt
$ dog -ds /Users/jiushu/Desktop/a.mp4.dog -k
Enter value for --secret-key (Both encrypt and decrypt require the same secret key):
Please wait...

[Decrypt file number]:1/1
[Source path]:/Users/jiushu/Desktop/a.mp4.dog
[Estimated time]:00:00:06
[>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>] 100%
[Decrypt result]:success
[Time consuming]:00:00:05,[Before size]:142.96MB,[After size]:107.22MB
[Target path]:/Users/jiushu/Desktop/a.mp4

>>> Operation complete <<<
[Total time]:00:00:05
[Results]:total files:1,successes:1,failures:0
```
### batch
When using batch mode, the source file path must use single quotation marks.
```shell
# encrypt
$ dog -es '/Users/jiushu/Desktop/*.mp4' -k
# decrypt
$ dog -s '/Users/jiushu/Desktop/*.dog' -k
```
### set target path
The default is stored in the user home directory.
```shell
# command -t, --target-path=<storage path>
# encrypt
$ dog -es /Users/jiushu/Desktop/a.mp4 -t /Users/jiushu/ -k
# decrypt
$ dog -s /Users/jiushu/a.mp4.dog -t /Users/jiushu/Desktop -k
```
### delete source file
```shell
# command -d, --delete
# encrypt
$ dog -des /Users/jiushu/Desktop/a.mp4 -k
# decrypt
$ dog -ds /Users/jiushu/Desktop/a.mp4.dog -k
```
### set target file name
The default is the same as the source file name.
```shell
# command -n, --set-name=<name>
# encrypt
$ dog -des /Users/jiushu/Desktop/a.mp4 -n b -k
# decrypt
$ dog -ds /Users/jiushu/Desktop/b.mp4.dog -n c -k
```
### the file is compressed after encryption
This operation is more time-consuming.
```shell
# command -c, --compress
# encrypt
$ dog -ces /Users/jiushu/Desktop/a.mp4 -k
```
### store secret-key
WARN:This operation is unsafe. Please use it with caution.
```shell
# command
alias dog = 'java -Ddog-store=true -Xms512m -Xmx512m -Xmn128m -jar dog-1.6.0.jar'
# store style(secret-key to base64)
[source]\:a.b,[source-size]\:0.02MB,[target]\:a.b.dog,[target-size]\:0.05MB=MTIzNDU2bmloYW96YXE\=
```
### file structure
|  file        | file extension name |  type          |   magic number |   location   |    amount     |     ascii    |
|  :-:         |        :-:          |  :-:           |   :-:          |    :-:       |     :-:       |      :-:     |
| DOG FORMAT   |        .dog         |  u4/32bit      |   0x19890225   |    header    |       1       |      ...     |
