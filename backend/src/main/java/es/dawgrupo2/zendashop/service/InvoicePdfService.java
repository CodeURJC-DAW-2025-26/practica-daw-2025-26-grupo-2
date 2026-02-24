package es.dawgrupo2.zendashop.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;

import es.dawgrupo2.zendashop.model.Order;
import es.dawgrupo2.zendashop.model.OrderItem;

@Service
public class InvoicePdfService {

    public byte[] generateInvoice(Order order) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {

            Document document = new Document(new Rectangle(595, 842), 48, 48, 50, 50);
            PdfWriter writer = PdfWriter.getInstance(document, os);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, new Color(0, 0, 0));
            Font infoLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(64, 64, 64));
            Font infoValueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(0, 0, 0));
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(255, 255, 255));
            Font tableCellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(0, 0, 0));
            Font totalsFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(0, 0, 0));

            Paragraph title = new Paragraph("Factura del pedido #" + order.getId(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(50f);
            document.add(title);

            Paragraph fecha = new Paragraph(
                    "Fecha del pedido: ",
                    infoLabelFont);
            fecha.add(new com.lowagie.text.Chunk(
                    order.getCreationDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    infoValueFont));
            fecha.setSpacingAfter(4f);
            document.add(fecha);

            Paragraph cliente = new Paragraph("Cliente: ", infoLabelFont);
            cliente.add(new com.lowagie.text.Chunk(
                    order.getUser().getName() + " " + order.getUser().getSurname(),
                    infoValueFont));
            cliente.setSpacingAfter(14f);
            document.add(cliente);

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 3.6f, 1.8f, 1.2f, 1.6f, 1.6f });
            table.setSpacingAfter(14f);

            addHeaderCell(table, "Producto", tableHeaderFont);
            addHeaderCell(table, "Referencia", tableHeaderFont);
            addHeaderCell(table, "Talla", tableHeaderFont);
            addHeaderCell(table, "Cantidad", tableHeaderFont);
            addHeaderCell(table, "Subtotal", tableHeaderFont);

            for (OrderItem item : order.getOrderItems()) {
                addBodyCell(table, item.getGarmentName(), tableCellFont, Element.ALIGN_LEFT);
                addBodyCell(table, item.getGarmentReference(), tableCellFont, Element.ALIGN_CENTER);
                addBodyCell(table, item.getSize(), tableCellFont, Element.ALIGN_CENTER);
                addBodyCell(table, String.valueOf(item.getQuantity()), tableCellFont, Element.ALIGN_RIGHT);
                addBodyCell(table, item.getSubtotal() + " €", tableCellFont, Element.ALIGN_RIGHT);
            }

            document.add(table);

            PdfPTable totals = new PdfPTable(2);
            totals.setWidthPercentage(45);
            totals.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totals.setWidths(new float[] { 2.2f, 1.2f });

            addTotalRow(totals, "Subtotal:", order.getSubtotal() + " €", false, totalsFont, tableCellFont);
            addTotalRow(totals, "Envío:", order.getShippingCost() + " €", false, totalsFont, tableCellFont);
            addTotalRow(totals, "Total:", order.getTotalPrice() + " €", true, totalsFont, tableCellFont);

            document.add(totals);

            try (InputStream is = new ClassPathResource("static/logo.png").getInputStream()) {
                Image logo = Image.getInstance(is.readAllBytes());
                logo.scaleToFit(120, 45);

                float x = 45;
                float y = 745;

                logo.setAbsolutePosition(x, y);
                writer.getDirectContent().addImage(logo);
                x = document.getPageSize().getWidth() - 50 - logo.getScaledWidth();
                y = 745;

                logo.setAbsolutePosition(x, y);
                writer.getDirectContent().addImage(logo);
            } catch (Exception ignored) {
            }

            document.close();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando factura PDF", e);
        }
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new com.lowagie.text.Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(8f);
        cell.setPaddingBottom(8f);
        cell.setBackgroundColor(new Color(33, 37, 41));
        cell.setBorderWidth(0.5f);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new com.lowagie.text.Phrase(text, font));
        cell.setHorizontalAlignment(align);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(7f);
        cell.setPaddingBottom(7f);
        cell.setPaddingLeft(6f);
        cell.setPaddingRight(6f);
        cell.setBorderWidth(0.5f);
        cell.setBorderColor(new Color(210, 210, 210));
        table.addCell(cell);
    }

    private void addTotalRow(PdfPTable table, String label, String value, boolean highlight, Font labelFont,
            Font valueFont) {
        PdfPCell c1 = new PdfPCell(new com.lowagie.text.Phrase(label, labelFont));
        PdfPCell c2 = new PdfPCell(new com.lowagie.text.Phrase(value, valueFont));

        c1.setBorderWidth(0f);
        c2.setBorderWidth(0f);
        c1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c1.setPaddingTop(4f);
        c1.setPaddingBottom(4f);
        c2.setPaddingTop(4f);
        c2.setPaddingBottom(4f);

        if (highlight) {
            c2.setBackgroundColor(new Color(245, 245, 245));
        }

        table.addCell(c1);
        table.addCell(c2);
    }
}