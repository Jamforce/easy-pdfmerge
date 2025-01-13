# Simple PdfMerge

A TypeScript library to merge multiple PDFs into a single PDF easily. 

This module uses [OpenPDF](https://github.com/LibrePDF/OpenPDF) and hence requires an installed java executable in the environment.
Java 11 or higher must be present.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)

## Installation

You can install the library via npm:

```ts
npm install -S simple-pdfmerge
```

## Usage

Merge multiple PDFs into a single file:

```ts
import { Merger } from 'simple-pdfmerge';

const input = ['samplePdf1.pdf', 'samplePdf2.pdf'];
const output = 'merged.pdf';

const result = await new Merger().exec(input, output);
if (result.code !== 0) {
    throw new Error(`Error merging PDFs: ${result.stderr}`);
}

```

Merge PDFs and stamp page numbers on the resulting file:

```ts
import { Merger } from 'simple-pdfmerge';

const input = ['samplePdf1.pdf', 'samplePdf2.pdf'];
const output = 'merged.pdf';
const pageNumberStamp = 'bottomRight';

const result = await new Merger().exec(input, output, pageNumberStamp);
if (result.code !== 0) {
    throw new Error(`Error merging PDFs: ${result.stderr}`);
}

```


## Contributing

Contributions are welcome! Please submit a pull request or open an issue to discuss your changes.

## License

This project is licensed under the MIT License. See the LICENSE file for details.
