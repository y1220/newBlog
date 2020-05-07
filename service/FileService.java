package it.course.myblog.service;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import it.course.myblog.entity.Post;
import it.course.myblog.repository.PostRepository;
import it.course.myblog.repository.UserRepository;

@Service
public class FileService {

	@Autowired
	PostRepository postRepository;

	@Autowired
	UserRepository userRepository;

	private static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.BLUE);
	private static Font FONT_CONTENT = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
	private static Font FONT_AUTHOR = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);
	private static Font FONT_DATE = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC, BaseColor.LIGHT_GRAY);

	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

	public InputStream createPdfFromPost(Post p) throws Exception {

		String createdBy = userRepository.findById(p.getCreatedBy()).get().getUsername();
		String title = p.getTitle();
		String content = p.getContent();
		List<String> tagList = p.getTags().stream().map(t -> t.getTagName()).collect(Collectors.toList());
		String createdAt = String.format("%1$tY-%1$tm-%1$td", p.getCreatedAt());

		Document document = new Document(PageSize.A4, 50, 50, 50, 50);

		ByteArrayOutputStream out = new ByteArrayOutputStream();


		// PdfWriter.getInstance(document, out);

		PdfWriter writer = PdfWriter.getInstance(document, out);

		// add header and footer

		HeaderFooterPageEvent event = new HeaderFooterPageEvent();
		writer.setPageEvent(event);


		document.open();

		addMetaData(document, title, createdBy, tagList.toString());

		Paragraph pCreatedAt = new Paragraph(createdAt, FONT_DATE);
		pCreatedAt.setAlignment(Element.ALIGN_RIGHT);
		document.add(pCreatedAt);

		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(30);
		table.setWidths(new int[] { 1, 2 });
		// String filename =
		// "C:/Users/ga2y1/Documents/workspace-spring-tool-suite-4-4.5.1.RELEASE/myblog/src/main/resources/img/logo.jpg";
		String filename = "src/main/resources/img/logo.jpg";
		table.addCell(createImageCell(filename));
		table.addCell(createTextCell(title));
		table.setHorizontalAlignment(Element.ALIGN_LEFT);
		document.add(table);
		/*
		 * Paragraph pTitle = new Paragraph(title, FONT_TITLE);
		 * pTitle.setAlignment(Element.ALIGN_LEFT); document.add(pTitle);
		 */
		Paragraph pAuthor = new Paragraph("Author: " + createdBy, FONT_AUTHOR);
		pAuthor.setAlignment(Element.ALIGN_LEFT);
		addEmptyLine(pAuthor, 1);
		document.add(pAuthor);

		Paragraph pContent = new Paragraph(content, FONT_CONTENT);
		pContent.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(pContent);
		/*
		 * String filename =
		 * "C:/Users/ga2y1/Documents/workspace-spring-tool-suite-4-4.5.1.RELEASE/myblog/src/main/resources/img/logo.jpg";
		 * Image image = Image.getInstance(filename); image.scaleToFit(50, 50);
		 * document.add(image);
		 */

		document.close();



		InputStream in = new ByteArrayInputStream(out.toByteArray());

		return in;

	}


	public static PdfPCell createImageCell(String path) throws DocumentException, IOException {
		Image img = Image.getInstance(path);
		img.scaleToFit(5, 5);
		PdfPCell cell = new PdfPCell(img, true);
		cell.setBorder(Rectangle.NO_BORDER);
		return cell;
	}

	public static PdfPCell createTextCell(String text) throws DocumentException, IOException {
		PdfPCell cell = new PdfPCell();
		// Paragraph p = new Paragraph(text);
		Paragraph p = new Paragraph(new Chunk(text, FONT_TITLE));
		p.setAlignment(Element.ALIGN_LEFT);
		cell.addElement(p);
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBorder(Rectangle.NO_BORDER);
		return cell;
	}

	private void addMetaData(Document document, String title, String author, String tagList) {
		document.addTitle(title);
		document.addKeywords(tagList);
		document.addAuthor(author);
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

}
