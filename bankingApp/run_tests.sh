#!/bin/bash
echo "Running Banking Application Test Suite..."
echo
java TestRunner.java

exit_code=$?

if [ $exit_code -ne 0 ]; then
    echo
    echo -e "\033[0;31mTests Failed!\033[0m"
    exit 1
else
    echo
    echo -e "\033[0;32mTests Passed!\033[0m"
    exit 0
fi
