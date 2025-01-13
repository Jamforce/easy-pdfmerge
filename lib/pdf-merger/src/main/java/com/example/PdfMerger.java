package com.example;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.*;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PdfMerger {

  private final Map<String, Float[]> positions = new HashMap<>();

  public PdfMerger() {
    positions.put("topLeft", new Float[]{90.0F, 815.0F});
    positions.put("topRight", new Float[]{550.0F, 815.0F});
    positions.put("bottomLeft", new Float[]{90.0F, 15.0F});
    positions.put("bottomRight", new Float[]{550.0F, 15.0F});
  }

  public void merge(String[] pdfPaths, String outputPath, String pageNumberStamp) {
        try (
          // Handle closable resources
          Document document = new Document();
          PdfCopy pdfCopy = new PdfCopy(document, new FileOutputStream(outputPath))
        ) {
            document.open();

            int currentPage = 1;
            // Loop through each PDF file
            for (String path : pdfPaths) {
                PdfReader reader = new PdfReader(path);
                int numberOfPages = reader.getNumberOfPages();

                // Loop through each page and copy it
                for (int i = 1; i <= numberOfPages; i++) {
                    PdfImportedPage page = pdfCopy.getImportedPage(reader, i);
                    if (pageNumberStamp != null && pageNumberStamp.length() > 0) {
                      PdfCopy.PageStamp pageStamp = pdfCopy.createPageStamp(page);
                      addPageNumber(pageStamp, currentPage, positions.get(pageNumberStamp));
                    }
                    pdfCopy.addPage(page);
                    currentPage++;
                }
                reader.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

  private void addPageNumber(PdfCopy.PageStamp pageStamp, int currentPage, Float[] coordinates) throws IOException {
    BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
    Font font = new Font(baseFont, 16, Font.NORMAL, Color.BLACK);;
    Phrase t = new Phrase(String.format("Pag. %d", currentPage), font);
    ColumnText.showTextAligned(
      pageStamp.getOverContent(), Element.ALIGN_RIGHT,
      t, coordinates[0], coordinates[1], 0);
    pageStamp.alterContents();
  }

}
