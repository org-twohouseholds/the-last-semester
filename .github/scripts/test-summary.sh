#!/usr/bin/env bash
# Writes a Markdown summary of JUnit results and JaCoCo coverage to stdout.
# Usage: .github/scripts/test-summary.sh >> "$GITHUB_STEP_SUMMARY"
set -u

echo "## Test results"
echo

results=$(find . -path '*/build/test-results/test/*.xml' 2>/dev/null | sort)
if [ -z "$results" ]; then
  echo "No test results found (no tests ran)."
else
  echo "| Module | Tests | Failed | Errors | Skipped |"
  echo "|---|---:|---:|---:|---:|"
  total_fail=0
  for module_dir in $(echo "$results" | sed -E 's#/build/test-results/test/.*##' | sort -u); do
    t=0; f=0; e=0; s=0
    for xml in $(echo "$results" | grep "^$module_dir/build/"); do
      header=$(grep -m1 '<testsuite ' "$xml")
      t=$((t + $(echo "$header" | sed -E 's/.* tests="([0-9]+)".*/\1/')))
      s=$((s + $(echo "$header" | sed -E 's/.* skipped="([0-9]+)".*/\1/')))
      f=$((f + $(echo "$header" | sed -E 's/.* failures="([0-9]+)".*/\1/')))
      e=$((e + $(echo "$header" | sed -E 's/.* errors="([0-9]+)".*/\1/')))
    done
    total_fail=$((total_fail + f + e))
    echo "| ${module_dir#./} | $t | $f | $e | $s |"
  done

  if [ "$total_fail" -gt 0 ]; then
    echo
    echo "### Failed tests"
    echo
    for xml in $results; do
      awk '
        /<testcase / {
          name = $0; sub(/.*classname="/, "", name); sub(/".*/, "", name)
          method = $0; sub(/.* name="/, "", method); sub(/".*/, "", method)
          current = name "." method
        }
        /<(failure|error) / {
          msg = $0; sub(/.*message="/, "", msg); sub(/".*/, "", msg)
          print "- `" current "`: " msg
        }
      ' "$xml" | sed -e 's/&lt;/</g' -e 's/&gt;/>/g' -e 's/&quot;/"/g' -e 's/&amp;/\&/g'
    done
  fi
fi

echo
echo "## Coverage (JaCoCo, instructions)"
echo
reports=$(find . -path '*/build/reports/jacoco/test/jacocoTestReport.xml' 2>/dev/null | sort)
if [ -z "$reports" ]; then
  echo "No coverage data (no tests ran)."
else
  echo "| Module | Covered | Total | Coverage |"
  echo "|---|---:|---:|---:|"
  for xml in $reports; do
    # The last INSTRUCTION counter in the file is the report-wide total.
    counter=$(grep -o '<counter type="INSTRUCTION"[^>]*>' "$xml" | tail -1)
    missed=$(echo "$counter" | sed -E 's/.*missed="([0-9]+)".*/\1/')
    covered=$(echo "$counter" | sed -E 's/.*covered="([0-9]+)".*/\1/')
    total=$((missed + covered))
    if [ "$total" -gt 0 ]; then
      pct=$(awk -v c="$covered" -v t="$total" 'BEGIN { printf "%.1f%%", 100 * c / t }')
    else
      pct="n/a"
    fi
    module=$(echo "$xml" | sed -E 's#^\./##; s#/build/reports/.*##')
    echo "| $module | $covered | $total | $pct |"
  done
fi

echo
echo "## Checkstyle"
echo
for xml in $(find . -path '*/build/reports/checkstyle/main.xml' 2>/dev/null | sort); do
  module=$(echo "$xml" | sed -E 's#^\./##; s#/build/reports/.*##')
  echo "- $module: $(grep -c '<error ' "$xml") warnings"
done
