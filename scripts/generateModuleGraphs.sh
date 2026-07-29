#!/usr/bin/env bash

set -euo pipefail

#######################################
# Configuration
#######################################

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"

OUTPUT_DIR="${PROJECT_ROOT}/docs/images/graphs"
TMP_DIR="${PROJECT_ROOT}/.module-graphs"

mkdir -p "$OUTPUT_DIR"
mkdir -p "$TMP_DIR"


#######################################
# Helpers
#######################################

error() {
    echo "ERROR: $1"
    exit 1
}


command_exists() {
    command -v "$1" >/dev/null 2>&1
}


#######################################
# Validate environment
#######################################

cd "$PROJECT_ROOT"


if [[ ! -f "./gradlew" ]]; then
    error "Gradle wrapper missing. Expected: ${PROJECT_ROOT}/gradlew"
fi


for dependency in dot; do
    if ! command_exists "$dependency"; then
        error "'$dependency' missing.

Install Graphviz:
winget install Graphviz.Graphviz"
    fi
done


if ! command_exists svgo; then
    echo "WARNING: svgo missing."
    echo "SVG optimization will be skipped."

    SVGO_AVAILABLE=false
else
    SVGO_AVAILABLE=true
fi


#######################################
# Arguments
#######################################

excluded_modules=()

while [[ $# -gt 0 ]]; do

    case "$1" in

        --exclude-module)
            [[ -z "${2:-}" ]] && error "Missing module name"

            excluded_modules+=("$2")

            shift 2
            ;;

        *)
            error "Unknown parameter: $1"
            ;;

    esac

done



is_excluded() {

    local module="$1"

    for excluded in "${excluded_modules[@]}"; do
        [[ "$module" == "$excluded" ]] && return 0
    done

    return 1
}



#######################################
# Module discovery
#######################################

echo "Discovering modules..."

module_paths=$(grep -rhoE 'include\("[^"]+"' settings.gradle.kts \
    | sed 's/include("//;s/"//' )


#######################################
# SVG generation
#######################################

generate_svg() {

    local gv="$1"
    local output="$2"


    if [[ "$SVGO_AVAILABLE" == true ]]; then

        dot -Tsvg "$gv" \
            | svgo \
                --multipass \
                --pretty \
                --output="$output" -

    else

        dot -Tsvg "$gv" \
            -o "$output"

    fi
}



#######################################
# README generation
#######################################

create_readme() {

    local module="$1"
    local graph="$2"


    local path="${module//:/\/}/README.md"

    if [[ ! -f "$path" ]]; then

        mkdir -p "$(dirname "$path")"


        cat > "$path" <<EOF
# ${module} Module

## Dependency Graph

![Dependency Graph](../../${OUTPUT_DIR#${PROJECT_ROOT}/}/${graph}.svg)
EOF

    fi

}



#######################################
# Generate root graph
#######################################

echo "Generating root dependency graph..."

ROOT_GV="${TMP_DIR}/root.gv"

./gradlew generateNiaModuleGraph \
    -Pmodules.graph.output.gv="$ROOT_GV" \
    --no-configure-on-demand


generate_svg \
    "$ROOT_GV" \
    "${OUTPUT_DIR}/dep_graph_root.svg"


echo "Generated root graph"



#######################################
# Generate module graphs
#######################################

while read -r module; do


    [[ -z "$module" ]] && continue


    if is_excluded "$module"; then
        echo "Skipping $module"
        continue
    fi


    echo "Generating graph for $module"


    file_name="dep_graph${module//:/_}"
    file_name="${file_name//-/_}"


    create_readme "$module" "$file_name"



    GV_FILE="${TMP_DIR}/${file_name}.gv"



    ./gradlew generateNiaModuleGraph \
        -Pmodules.graph.output.gv="$GV_FILE" \
        -Pmodules.graph.of.module="$module" \
        --no-configure-on-demand



    generate_svg \
        "$GV_FILE" \
        "${OUTPUT_DIR}/${file_name}.svg"



done <<< "$module_paths"



echo ""
echo "=================================="
echo "Module graphs generated successfully"
echo "Output:"
echo "$OUTPUT_DIR"
echo "=================================="