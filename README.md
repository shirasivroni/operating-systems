# Operating Systems - Course Assignments

This repository contains solutions to assignments from the Operating Systems course at Reichman University.
The assignments cover topics such as system calls, process management, interprocess communication (IPC), multithreading, and synchronization, implemented in C and Java.

---

## Repository Structure

```
Operating-Systems-Course-Assignments/
|
├── Assignment1_FileCopy_C/
│   ├── file_copy.c
│
├── Assignment2_URLChecker_C/
│   ├── url_response_checker.c
|
├── Assignment3_Synchronization_Java/
│   ├── DiskSearcher/
│   |   ├── DiskSearcher.java
│   |   ├── Searcher.java
│   |   ├── Scouter.java
│   |   ├── Copier.java
│   |   └── SynchronizedQueue.java
│   └── ShakespearePerformanceTest/
│       ├── Main.java
│       └── Worker.java
```

---

## Assignments Overview

### Assignment 1 - File Copy Utility (C)
- A basic C program that copies the contents of one file to another using a user-defined buffer size.
- Implements low-level system calls: `open`, `read`, `write`, `close`.
- Includes error handling and optional force-overwrite support.

### Assignment 2 - Parallel URL Checker (C)
- A C program that uses multiple processes to check response times for a list of URLs.
- Utilizes `fork`, `pipe`, and `libcurl` to manage concurrent HTTP HEAD requests.
- Demonstrates process management and IPC (Inter-Process Communication).

### Assignment 3 - Disk Searcher and Synchronization (Java)
- A multithreaded Java application that:
  - Scans directories to find files matching a given pattern and extension.
  - Copies matching files to a target directory.
  - Synchronizes access to shared queues with custom `SynchronizedQueue`.

### Assignment 3 - Shakespeare Performance Test (Java)
- A Java program that benchmarks file processing performance with and without thread pools.
- Analyzes the impact of multithreading on large file operations (Shakespeare's complete works).

---

## Technologies Used
- **Languages:** C, Java
- **Libraries:**
  - `libcurl` (C URL requests)
  - Java Concurrency Utilities (`ExecutorService`, Threads)

---

## How to Run

### C Assignments
- Compile using `gcc`:
  ```bash
  gcc file_copy.c -o file_copy
  ./file_copy [options]
  
  gcc url_response_checker.c -lcurl -o url_checker
  ./url_checker [number_of_processes] [filename]
  ```

### Java Assignments
- Compile using `javac`:
  ```bash
  javac DiskSearcher/*.java
  java DiskSearcher.DiskSearcher [pattern] [extension] [root_dir] [dest_dir] [#searchers] [#copiers]

  javac ShakespearePerformanceTest/*.java
  java ShakespearePerformanceTest.Main
  ```

---

## License
This repository is for educational purposes only, developed as part of the Operating Systems course at Reichman University.

