#!/bin/bash
#
# Copyright 2024 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
#
# Script to generate dependency graphs for each of the modules using Gradle Wrapper.
# The --exclude-module parameter can be used to exclude modules from the root dependency graph.
#
# Usage: ./generateModuleGraphs.sh --exclude-module :benchmarks --exclude-module :lint --exclude-module :ui-test-hilt-manifest

# Ensure Gradle Wrapper exists
if [[ ! -f "gradlew" ]]; then
    echo "Gradle Wrapper (gradlew) not found. Please run './gradlew wrapper' first."
    exit 1
fi

# Check if the required dependencies are installed
dependencies=(dot svgo)
for cmd in "${dependencies[@]}"; do
    if ! command -v "$cmd" &> /dev/null; then
        echo "Error: '$cmd' is required but not found."
        exit 1
    fi
done

# Check for a grep version that supports Perl regex
#grep_command="grep"
#if ! grep -P "" /dev/null &> /dev/null; then
    #if command -v ggrep &> /dev/null; then
        #grep_command="ggrep"
    #else
        #echo "Error: A grep version that supports Perl regex is required. Install GNU grep (brew install grep)."
        #exit 1
    #fi
#fi

# Parse command-line arguments for excluded modules
excluded_modules=()
while [[ $# -gt 0 ]]; do
    case "$1" in
        --exclude-module)
            excluded_modules+=("$2")
            shift # Past argument
            shift # Past value
            ;;
        *)
            echo "Unknown parameter: $1"
            exit 1
            ;;
    esac
done

# Get the module paths
module_paths=$(grep -o 'include(".*' settings.gradle.kts | sed 's/include("//;s/".*//')

# Ensure output directory exists
mkdir -p docs/images/graphs/

# Function to check and create README.md for modules that don't have one.
create_readme() {
    local module_path="$1"
    local file_name="$2"
    local readme_path="${module_path//:/\/}/README.md"
    
    if [[ ! -f "$readme_path" ]]; then
        echo "Creating README.md for ${module_path}"
        local depth=$(awk -F: '{print NF-1}' <<< "$module_path")
        local relative_image_path="../"
        for ((i=1; i<$depth; i++)); do relative_image_path+="../"; done
        relative_image_path+="docs/images/graphs/${file_name}.svg"

        cat <<EOF > "$readme_path"
# ${module_path} Module
## Dependency Graph
![Dependency Graph](${relative_image_path})
EOF
    fi
}

# Generate dependency graphs
while read -r module_path; do
    if [[ ! " ${excluded_modules[@]} " =~ " ${module_path} " ]]; then
        file_name="dep_graph${module_path//:/_}"
        file_name="${file_name//-/_}"
        create_readme "$module_path" "$file_name"

        # Generate Graphviz file using Gradle Wrapper
        ./gradlew generateModulesGraphvizText \
          -Pmodules.graph.output.gv="/tmp/${file_name}.gv" \
          -Pmodules.graph.of.module="${module_path}" </dev/null

        # Convert Graphviz file to SVG and optimize
        dot -Tsvg "/tmp/${file_name}.gv" | svgo --multipass --pretty --output="docs/images/graphs/${file_name}.svg" -
        rm "/tmp/${file_name}.gv"
    fi
done <<< "$module_paths"
