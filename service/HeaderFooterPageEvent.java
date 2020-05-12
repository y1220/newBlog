package it.course.myblog.service;



import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

public class HeaderFooterPageEvent extends PdfPageEventHelper {

	private PdfTemplate t;
	private Image total;

	private static Font FONT_FOOTER = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.ITALIC);

	public void onOpenDocument(PdfWriter writer, Document document) {
		t = writer.getDirectContent().createTemplate(30, 16);
		try {
			total = Image.getInstance(t);
			total.setRole(PdfName.ARTIFACT);
		} catch (DocumentException de) {
			throw new ExceptionConverter(de);
		}
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		try {
			addFooter(writer, document);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}



	private void addFooter(PdfWriter writer, Document document) throws DocumentException {
		PdfPTable footer = new PdfPTable(new float[] { 95, 5 });

		footer.setPaddingTop(20);

		// set defaults
		// footer.setWidths(new int[] { 5, 2 });
		footer.setTotalWidth(document.getPageSize().getWidth() - 75);
		// footer.setLockedWidth(true);
		footer.getDefaultCell().setFixedHeight(40);
		// footer.getDefaultCell().setBorder(Rectangle.TOP);
		// footer.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

		// add current page count
		footer.getDefaultCell().disableBorderSide(Rectangle.BOX);
		footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
		footer.addCell(new Phrase(String.format("Page %d of", writer.getPageNumber()), FONT_FOOTER));

		// add placeholder for total page count
		PdfPCell totalPageCount = new PdfPCell(total);
		totalPageCount.disableBorderSide(Rectangle.BOX);
		// totalPageCount.setBorder(Rectangle.TOP);
		// totalPageCount.setBorderColor(BaseColor.LIGHT_GRAY);
		totalPageCount.setPaddingTop(1);
		footer.addCell(totalPageCount);


		// write page
		PdfContentByte canvas = writer.getDirectContent();
		canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
		footer.writeSelectedRows(0, -1, 34, 50, canvas);
		canvas.endMarkedContentSequence();
	}

	public void onCloseDocument(PdfWriter writer, Document document) {
		int totalLength = String.valueOf(writer.getPageNumber()).length();
		int totalWidth = totalLength * 5;
		ColumnText.showTextAligned(t, Element.ALIGN_RIGHT,
				new Phrase(String.valueOf(writer.getPageNumber()), FONT_FOOTER), totalWidth, 6, 0);
	}
}
