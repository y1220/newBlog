package it.course.myblog.service;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import it.course.myblog.entity.Blacklist;
import it.course.myblog.entity.Comment;
import it.course.myblog.entity.Post;
import it.course.myblog.entity.PostViewed;
import it.course.myblog.entity.Role;
import it.course.myblog.entity.RoleName;
import it.course.myblog.entity.Users;
import it.course.myblog.payload.response.AuthorExcel;
import it.course.myblog.payload.response.PostExcel;
import it.course.myblog.payload.response.ReaderExcel;
import it.course.myblog.repository.BlacklistRepository;
import it.course.myblog.repository.CommentRepository;
import it.course.myblog.repository.PostRepository;
import it.course.myblog.repository.PostViewedRepository;
import it.course.myblog.repository.RoleRepository;
import it.course.myblog.repository.UserRepository;
import one.util.streamex.StreamEx;


@Service
public class FileService {

	@Autowired
	PostRepository postRepository;

	@Autowired
	PostViewedRepository postViewdRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	CommentRepository commentRepository;

	@Autowired
	BlacklistRepository blacklistRepository;

	@Autowired
	PostViewedRepository postViewedRepository;

	// attributi PDF
	private static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
	private static Font FONT_CONTENT = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
	private static Font FONT_AUTHOR = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);
	private static Font FONT_DATE = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC, BaseColor.LIGHT_GRAY);
	private static Font FONT_PAGE_NUMBER = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.ITALIC, BaseColor.LIGHT_GRAY);

	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

	// inizio metodo generazione PDF
	public InputStream createPdfFromPost(Post p) throws Exception {

		String createdBy = userRepository.findById(p.getCreatedBy()).get().getUsername();
		String title = p.getTitle();
		String content = p.getContent();
		List<String> tagList = p.getTags().stream().map(t -> t.getTagName()).collect(Collectors.toList());
		String createdAt = String.format("%1$tY-%1$tm-%1$td", p.getCreatedAt());

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		Document document = new Document(PageSize.A4, 50, 50, 50, 50);
		PdfWriter.getInstance(document, out);

		document.open();

		addMetaData(document, title, createdBy, tagList.toString());

		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		addCustomRows(table);
		addRows(table, title);

		document.add(table);

		Paragraph pCreatedAt = new Paragraph(createdAt, FONT_DATE);
		pCreatedAt.setAlignment(Element.ALIGN_RIGHT);
		document.add(pCreatedAt);

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

		document.close();

		InputStream in = new ByteArrayInputStream(out.toByteArray());

		// pagination
		PdfReader reader = new PdfReader(in);
		int n = reader.getNumberOfPages();
		PdfStamper stamper = new PdfStamper(reader, out);
		PdfContentByte pagecontent;

		for (int i = 0; i < n;) {
			pagecontent = stamper.getUnderContent(++i);
			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT,
					new Phrase(String.format("%s of %s", i, n), FONT_PAGE_NUMBER), document.right(),
					document.bottom() - 10, 0);
		}

		stamper.close();

		in = new ByteArrayInputStream(out.toByteArray());

		return in;

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

	private void addRows(PdfPTable table, String text) {
		Paragraph p = new Paragraph(text, FONT_TITLE);
		p.setAlignment(Element.ALIGN_LEFT);

		PdfPCell textCell = new PdfPCell(p);
		textCell.setBorder(Rectangle.NO_BORDER);
		textCell.setPaddingLeft(-190);
		table.addCell(textCell);
	}

	private void addCustomRows(PdfPTable table) throws URISyntaxException, BadElementException, IOException {
		Path path = Paths.get(ClassLoader.getSystemResource("img/logo.jpg").toURI());
		Image img = Image.getInstance(path.toAbsolutePath().toString());
		img.scalePercent(50);

		PdfPCell imageCell = new PdfPCell(img);
		imageCell.setBorder(Rectangle.NO_BORDER);
		table.addCell(imageCell);
	}

	// fine metodo generazione PDF

	// inizio metodo generazione XLS
	HSSFFont font = null;

	public InputStream createExcel() throws FileNotFoundException, IOException {
		HSSFWorkbook workbook = new HSSFWorkbook();

		createExcelAuthorReport(workbook);
		createExcelReaderReport(workbook);
		createExcelPostReport(workbook);

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		workbook.write(out);
		workbook.close();

		return new ByteArrayInputStream(out.toByteArray());
	}

	public void createExcelPostReport(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.createSheet("Post Report");

		font = sheet.getWorkbook().createFont();
		font.setBold(true);

		CellStyle cellBoldCenterClear = createSheet(sheet, true, HorizontalAlignment.CENTER, false);
		CellStyle cellBoldRightClear = createSheet(sheet, true, HorizontalAlignment.RIGHT, false);
		CellStyle cellBoldRightColor = createSheet(sheet, true, HorizontalAlignment.RIGHT, true);
		CellStyle cellClear = createSheet(sheet, false, null, false);
		CellStyle cellClearRight = createSheet(sheet, false, HorizontalAlignment.RIGHT, false);
		CellStyle cellClearCenter = createSheet(sheet, false, HorizontalAlignment.CENTER, false);
		CellStyle cellColor = createSheet(sheet, false, null, true);
		CellStyle cellColorRight = createSheet(sheet, false, HorizontalAlignment.RIGHT, true);
		CellStyle cellColorCenter = createSheet(sheet, false, HorizontalAlignment.CENTER, true);

		List<Post> allPosts = postRepository.findAll();
		List<PostViewed> allPostViewed = postViewedRepository.findAll();

		List<PostExcel> postExcel = new ArrayList<PostExcel>();
		for (Post post : allPosts) {
			List<PostViewed> postsViewed = allPostViewed.stream().filter(p -> (post.getId() == p.getPost().getId()))
					.collect(Collectors.toList());

			Long idPost = post.getId();
			String title = post.getTitle();

			boolean isFree = (post.getCredit().getCreditImport() == 0);

			Long totalViews = Long.valueOf(postsViewed.size());

			Long uniqueViews = Long.valueOf(StreamEx.of(postsViewed).distinct(PostViewed::getIp).toList().size());

			boolean isBanned = false;
			LocalDate loc = LocalDate.now();
			for (Blacklist bl : post.getBlacklists()) {
				if (bl.getCommentId() == 0 && bl.getBlacklistedUntil()
						.isAfter(LocalDate.of(loc.getYear(), loc.getMonthValue(), loc.getDayOfMonth()))) {
					isBanned = true;
					break;
				}
			}

			Double avgRating = ((post.getAvgRating() == null) ? 0 : post.getAvgRating());

			Long numberOfPurchases = Long.valueOf(post.getUsersWhoBought().size());

			postExcel.add(new PostExcel(idPost, title, (isFree ? "Y" : "N"), totalViews, uniqueViews,
					(isBanned ? "Y" : "N"), avgRating, numberOfPurchases));
		}

		String[] header = { "Post", "Title", "is Free", "Total Views", "Unique Views", "is Banned", "AVG Rating",
				"Number of Purchases" };

		int rowCount = 0;

		Row row = sheet.createRow(++rowCount);
		for (int j = 1; j <= header.length; j++) {
			Cell cell = row.createCell(j);
			cell.setCellStyle(cellBoldCenterClear);
			cell.setCellValue(header[j - 1]);
		}

		for (PostExcel post : postExcel) {
			row = sheet.createRow(++rowCount);

			Cell cell = row.createCell(1);
			cell.setCellValue(post.getIdPost());
			colorCell(cellClear, cellColor, rowCount, cell);

			cell = row.createCell(2);
			cell.setCellValue(post.getTitle());
			colorCell(cellClear, cellColor, rowCount, cell);

			cell = row.createCell(3);
			cell.setCellValue(post.getIsFree());
			colorCell(cellClearCenter, cellColorCenter, rowCount, cell);

			cell = row.createCell(4);
			cell.setCellValue(post.getTotalViews());
			colorCell(cellClear, cellColor, rowCount, cell);

			cell = row.createCell(5);
			cell.setCellValue(post.getUniqueViews());
			colorCell(cellClear, cellColor, rowCount, cell);

			cell = row.createCell(6);
			cell.setCellValue(post.getIsBanned());
			colorCell(cellClearCenter, cellColorCenter, rowCount, cell);

			cell = row.createCell(7);
			cell.setCellValue(new DecimalFormat("0.00").format(post.getAvgRating()));
			colorCell(cellClearRight, cellColorRight, rowCount, cell);

			cell = row.createCell(8);
			cell.setCellValue(post.getNumberOfPurchases());
			colorCell(cellClear, cellColor, rowCount, cell);
		}

		String[] footer = { "COUNT(B3:B" + (postExcel.size() + 2) + ")", "", "",
				"SUM(E3:E" + (postExcel.size() + 2) + ")", "SUM(F3:F" + (postExcel.size() + 2) + ")",
				"COUNTIF(G3:G" + (postExcel.size() + 2) + ", \"Y\")", "", "SUM(I3:I" + (postExcel.size() + 2) + ")" };

		row = sheet.createRow(++rowCount);
		for (int j = 1; j <= footer.length; j++) {
			Cell cell = row.createCell(j);
			colorCell(cellBoldRightClear, cellBoldRightColor, rowCount, cell);
			if (footer[j - 1] != "")
				cell.setCellFormula(footer[j - 1]);
			else
				cell.setCellValue(footer[j - 1]);
		}

		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6);
		sheet.autoSizeColumn(7);
		sheet.autoSizeColumn(8);
	}

	public void createExcelReaderReport(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.createSheet("Reader Report");

		font = sheet.getWorkbook().createFont();
		font.setBold(true);

		CellStyle cellBoldCenterClear = createSheet(sheet, true, HorizontalAlignment.CENTER, false);
		CellStyle cellBoldCenterColor = createSheet(sheet, true, HorizontalAlignment.CENTER, true);
		CellStyle cellBoldRightClear = createSheet(sheet, true, HorizontalAlignment.RIGHT, false);
		CellStyle cellBoldRightColor = createSheet(sheet, true, HorizontalAlignment.RIGHT, true);
		CellStyle cellClear = createSheet(sheet, false, null, false);
		CellStyle cellColor = createSheet(sheet, false, null, true);

		Role roleReader = roleRepository.findByName(RoleName.ROLE_READER).get();

		List<Comment> allComments = commentRepository.findAll();

		List<Long> usersWhoCommented = allComments.stream().map(comment -> comment.getCreatedBy()).distinct()
				.collect(Collectors.toList());

		/*
		 * add all users who have the role READER or has at least 1 comment with
		 * DISTINCT
		 */
		List<Users> allUsers = userRepository.findByRolesOrIdIn(roleReader, usersWhoCommented).stream().distinct()
				.collect(Collectors.toList());

		List<Long> idComments = allComments.stream().map(comment -> comment.getId()).collect(Collectors.toList());

		/* select only the relevant records */
		List<Blacklist> allBlacklists = blacklistRepository.findByCommentIdInAndBlacklistedUntilAfter(idComments,
				LocalDate.now());

		List<ReaderExcel> readerExcel = new ArrayList<ReaderExcel>();
		for (Users user : allUsers) {
			List<Comment> comments = allComments.stream().filter(c -> (user.getId() == c.getCreatedBy()))
					.collect(Collectors.toList());

			Long totalComments = Long.valueOf(comments.size());
			Long publishedComments = Long
					.valueOf(comments.stream().filter(c -> c.isVisible()).collect(Collectors.toList()).size());

			List<Comment> commentsNotVisible = comments.stream().filter(p -> !p.isVisible())
					.collect(Collectors.toList());

			Long totalBannedComments = Long.valueOf(0);
			Long commentsToCheck = Long.valueOf(0);
			for (Comment c : commentsNotVisible) {
				List<Blacklist> postBlacklist = allBlacklists.stream().filter(b -> (b.getCommentId() == c.getId()))
						.collect(Collectors.toList());

				if (postBlacklist.size() > 0)
					totalBannedComments++;
				else
					commentsToCheck++;
			}

			Long boughPosts = Long.valueOf(user.getPosts().size());
			Long credits = Long.valueOf(user.getCredit());

			readerExcel.add(new ReaderExcel(user.getUsername(), totalComments, publishedComments, commentsToCheck,
					totalBannedComments, boughPosts, credits));
		}

		String[] header = { "Reader", "Total Comments", "Published Comments", "Comments to Check", "Banned Comments",
				"Bought Posts", "Credits" };

		int rowCount = 0;

		Row row = sheet.createRow(++rowCount);
		for (int j = 1; j <= header.length; j++) {
			Cell cell = row.createCell(j);
			cell.setCellStyle(cellBoldCenterClear);
			cell.setCellValue(header[j - 1]);
		}

		for (ReaderExcel reader : readerExcel) {
			row = sheet.createRow(++rowCount);

			Cell cell = row.createCell(1);
			cell.setCellValue(reader.getUsername());
			colorCell(cellClear, cellColor, rowCount, cell);

			cell = row.createCell(2);
			cell.setCellValue(reader.getTotalComments());
			colorCell(cellClear, cellColor, rowCount, cell);

			cell = row.createCell(3);
			cell.setCellValue(reader.getPublishedComments());
			colorCell(cellClear, cellColor, rowCount, cell);

			cell = row.createCell(4);
			cell.setCellValue(reader.getCommentsToCheck());
			colorCell(cellClear, cellColor, rowCount, cell);

			cell = row.createCell(5);
			cell.setCellValue(reader.getBannedComments());
			colorCell(cellClear, cellColor, rowCount, cell);

			cell = row.createCell(6);
			cell.setCellValue(reader.getBoughtPosts());
			colorCell(cellClear, cellColor, rowCount, cell);

			cell = row.createCell(7);
			cell.setCellValue(reader.getCredits());
			colorCell(cellClear, cellColor, rowCount, cell);
		}

		String[] footer = { "Total", "SUM(C3:C" + (readerExcel.size() + 2) + ")",
				"SUM(D3:D" + (readerExcel.size() + 2) + ")", "SUM(E3:E" + (readerExcel.size() + 2) + ")",
				"SUM(F3:F" + (readerExcel.size() + 2) + ")", "SUM(G3:G" + (readerExcel.size() + 2) + ")",
				"SUM(H3:H" + (readerExcel.size() + 2) + ")" };

		row = sheet.createRow(++rowCount);
		for (int j = 1; j <= footer.length; j++) {
			Cell cell = row.createCell(j);
			if (j == 1) {
				colorCell(cellBoldCenterClear, cellBoldCenterColor, rowCount, cell);
				cell.setCellValue(footer[j - 1]);
			} else {
				colorCell(cellBoldRightClear, cellBoldRightColor, rowCount, cell);
				cell.setCellFormula(footer[j - 1]);
			}
		}

		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);
		sheet.autoSizeColumn(6);
		sheet.autoSizeColumn(7);
	}

	public void createExcelAuthorReport(HSSFWorkbook workbook) {
		HSSFSheet sheet = workbook.createSheet("Author Report");

		font = sheet.getWorkbook().createFont();
		font.setBold(true);

		CellStyle cellBoldCenterClear = createSheet(sheet, true, HorizontalAlignment.CENTER, false);
		CellStyle cellBoldCenterColor = createSheet(sheet, true, HorizontalAlignment.CENTER, true);
		CellStyle cellBoldRightClear = createSheet(sheet, true, HorizontalAlignment.RIGHT, false);
		CellStyle cellBoldRightColor = createSheet(sheet, true, HorizontalAlignment.RIGHT, true);
		CellStyle cellClear = createSheet(sheet, false, null, false);
		CellStyle cellColor = createSheet(sheet, false, null, true);

		Role roleEditor = roleRepository.findByName(RoleName.ROLE_EDITOR).get();

		List<Post> allPosts = postRepository.findAll();

		List<Long> usersWhoPosted = allPosts.stream().map(post -> post.getCreatedBy()).distinct()
				.collect(Collectors.toList());

		/*
		 * add all users who have the role EDITOR or has created at least 1 post with
		 * DISTINCT
		 */
		List<Users> allUsers = userRepository.findByRolesOrIdIn(roleEditor, usersWhoPosted).stream().distinct()
				.collect(Collectors.toList());

		List<Long> idPosts = allPosts.stream().map(post -> post.getId()).collect(Collectors.toList());

		/* select only the relevant records */
		List<Blacklist> allBlacklists = blacklistRepository.findByPostIdInAndCommentId(idPosts, Long.valueOf(0));

		List<AuthorExcel> authorExcel = new ArrayList<AuthorExcel>();
		for (Users user : allUsers) {
			List<Post> posts = allPosts.stream().filter(p -> (user.getId() == p.getCreatedBy()))
					.collect(Collectors.toList());

			Long totalPosts = Long.valueOf(posts.size());
			Long publishedPosts = Long.valueOf(
					posts.stream().filter(p -> (p.isVisible() && p.isApproved())).collect(Collectors.toList()).size());
			Long postsToCheck = Long.valueOf(
					posts.stream().filter(p -> !p.isVisible() && p.isApproved()).collect(Collectors.toList()).size());

			List<Post> postToVerify = posts.stream().filter(p -> (!p.isVisible() && !p.isApproved()))
					.collect(Collectors.toList());

			Long totalBannedPosts = Long.valueOf(0);
			for (Post p : postToVerify) {
				List<Blacklist> postBlacklist = allBlacklists.stream()
						.filter(b -> ((p.getId() == b.getPost().getId()) && b.isVerified() == true))
						.collect(Collectors.toList());

				if (postBlacklist.size() > 0)
					totalBannedPosts++;
			}

			authorExcel.add(
					new AuthorExcel(user.getUsername(), totalPosts, publishedPosts, postsToCheck, totalBannedPosts));
		}

		String[] header = { "Author", "Total Posts", "Published Posts", "Posts to Check", "Banned Posts" };

		int rowCount = 0;

		Row row = sheet.createRow(++rowCount);
		for (int j = 1; j <= header.length; j++) {
			Cell cell = row.createCell(j);
			cell.setCellStyle(cellBoldCenterClear);
			cell.setCellValue(header[j - 1]);
		}

		for (AuthorExcel author : authorExcel) {
			row = sheet.createRow(++rowCount);

			Cell cell = row.createCell(1);
			cell.setCellValue(author.getAuthor());
			colorCell(cellClear, cellColor, rowCount, cell);

			cell = row.createCell(2);
			cell.setCellValue(author.getTotalPosts());
			colorCell(cellClear, cellColor, rowCount, cell);

			cell = row.createCell(3);
			cell.setCellValue(author.getPublishedPosts());
			colorCell(cellClear, cellColor, rowCount, cell);

			cell = row.createCell(4);
			cell.setCellValue(author.getPostsToCheck());
			colorCell(cellClear, cellColor, rowCount, cell);

			cell = row.createCell(5);
			cell.setCellValue(author.getBannedPosts());
			colorCell(cellClear, cellColor, rowCount, cell);
		}

		String[] footer = { "Total", "SUM(C3:C" + (authorExcel.size() + 2) + ")",
				"SUM(D3:D" + (authorExcel.size() + 2) + ")", "SUM(E3:E" + (authorExcel.size() + 2) + ")",
				"SUM(F3:F" + (authorExcel.size() + 2) + ")" };

		row = sheet.createRow(++rowCount);
		for (int j = 1; j <= footer.length; j++) {
			Cell cell = row.createCell(j);
			if (j == 1) {
				colorCell(cellBoldCenterClear, cellBoldCenterColor, rowCount, cell);
				cell.setCellValue(footer[j - 1]);
			} else {
				colorCell(cellBoldRightClear, cellBoldRightColor, rowCount, cell);
				cell.setCellFormula(footer[j - 1]);
			}
		}

		sheet.autoSizeColumn(1);
		sheet.autoSizeColumn(2);
		sheet.autoSizeColumn(3);
		sheet.autoSizeColumn(4);
		sheet.autoSizeColumn(5);
	}

	private CellStyle createSheet(HSSFSheet sheet, boolean bold, HorizontalAlignment alignment, boolean colored) {
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();

		if (alignment != null)
			cellStyle.setAlignment(alignment);

		if (bold)
			cellStyle.setFont(font);

		if (colored)
			cellStyle.setFillForegroundColor(HSSFColorPredefined.GREY_25_PERCENT.getIndex());
		else
			cellStyle.setFillForegroundColor(HSSFColorPredefined.WHITE.getIndex());

		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		setBorders(cellStyle);

		cellStyle.setLocked(true);

		return cellStyle;
	}

	private void setBorders(CellStyle cellClear) {
		cellClear.setBorderRight(BorderStyle.THIN);
		cellClear.setRightBorderColor(HSSFColorPredefined.BLACK.getIndex());
		cellClear.setBorderLeft(BorderStyle.THIN);
		cellClear.setLeftBorderColor(HSSFColorPredefined.BLACK.getIndex());
	}

	private void colorCell(CellStyle cellClear, CellStyle cellColor, int rowCount, Cell cell) {
		if (rowCount % 2 == 0)
			cell.setCellStyle(cellColor);
		else
			cell.setCellStyle(cellClear);
	}

}
