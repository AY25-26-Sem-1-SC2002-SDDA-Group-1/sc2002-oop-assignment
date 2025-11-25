const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

// Read the UML.md file
const umlContent = fs.readFileSync('docs/UML.md', 'utf8');

// Create output directory for SVGs
const outputDir = 'uml-diagrams-svg';
if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir);
}

let diagramCount = 0;

// Find all mermaid code blocks
const mermaidRegex = /```mermaid\n([\s\S]*?)```/g;
let match;
let blockIndex = 0;

while ((match = mermaidRegex.exec(umlContent)) !== null) {
    const mermaidCode = match[1];

    // Find the title of this diagram (look backwards for ## heading)
    const beforeMatch = umlContent.substring(0, match.index);
    const titleMatch = beforeMatch.match(/## ([^\n]+)/g);
    const title = titleMatch && titleMatch.length > 0
        ? titleMatch[titleMatch.length - 1].replace('## ', '').replace(/[^\w\s-]/g, '').replace(/\s+/g, '_').toLowerCase()
        : `diagram_${blockIndex + 1}`;

    // Create temporary mermaid file
    const tempFile = `temp_diagram_${blockIndex}.mmd`;
    fs.writeFileSync(tempFile, mermaidCode);

    // Convert to SVG
    const outputFile = path.join(outputDir, `${title}.svg`);
    try {
        execSync(`mmdc -i ${tempFile} -o ${outputFile} -t default -b transparent`);
        console.log(`Generated: ${outputFile}`);
        diagramCount++;
    } catch (error) {
        console.error(`Failed to generate ${outputFile}:`, error.message);
    }

    // Clean up temp file
    fs.unlinkSync(tempFile);
    blockIndex++;
}

console.log(`\nConversion complete! Generated ${diagramCount} SVG files in the '${outputDir}' directory.`);