#!/usr/bin/env bash

set -euo pipefail

if [[ $# -ne 1 ]]; then
  echo "Error: No day provided. Usage: $0 <day>"
  exit 1
fi

day="$1"

if ! [[ "$day" =~ ^[0-9]+$ ]] || (( day < 1 || day > 25 )); then
  echo "Error: '$day' is not a valid number. Please provide a day between 1 and 25."
  exit 1
fi

output_file="src/days/${day}.cpp"
if [ -f "${output_file}" ]; then
    echo "${output_file} already exists, exiting..."
    exit 1
fi

file="src/days/index.h"

if [[ ! -f "$file" ]]; then
  echo "Error: File '$file' does not exist."
  exit 1
fi

sed -i "" "s/inline void run${day}(Utils::Mode mode, int part) {};/void run${day}(Utils::Mode mode, int part);/g" "$file"
echo "Successfully updated definition for day $day in $file."

sed "s/{{n}}/$day/g" day-n.cpp.template > "${output_file}"
echo "Successfully created ${output_file}"
