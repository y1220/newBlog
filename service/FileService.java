package it.course.myblog.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
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
import it.course.myblog.entity.Users;
import it.course.myblog.repository.BlacklistRepository;
import it.course.myblog.repository.CommentRepository;
import it.course.myblog.repository.PostRepository;
import it.course.myblog.repository.PostViewedRepository;
import it.course.myblog.repository.UserRepository;
import one.util.streamex.StreamEx;

@Service
public class FileService {

	@Autowired
	PostRepository postRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	PostViewedRepository postViewdRepository;

	@Autowired
	BlacklistRepository blacklistRepository;

	@Autowired
	CommentRepository commentRepository;

	private static Font FONT_TITLE = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.BLUE);
	private static Font FONT_CONTENT = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
	private static Font FONT_AUTHOR = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);
	private static Font FONT_DATE = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.ITALIC, BaseColor.LIGHT_GRAY);
	private static Font FONT_PAGE_NUMBER = new Font(Font.FontFamily.TIMES_ROMAN, 9, Font.ITALIC, BaseColor.LIGHT_GRAY);

	public InputStream createPdfFromPost(Post p) throws Exception {

		String createdBy = userRepository.findById(p.getCreatedBy()).get().getUsername();
		String title = p.getTitle();
		String content = p.getContent();
		List<String> tagList = p.getTags().stream().map(t -> t.getTagName()).collect(Collectors.toList());
		String createdAt = String.format("%1$tY-%1$tm-%1$td", p.getCreatedAt());
		String logoPath = "src/main/resources/img/logo.jpg";

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		Document document = new Document(PageSize.A4, 50, 50, 50, 50);
		PdfWriter.getInstance(document, out);

		document.open();

		addMetaData(document, title, createdBy, tagList.toString());

		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 1, 2, 3 });
		table.addCell(createImageCell(logoPath));

		Paragraph pTitle = new Paragraph(title, FONT_TITLE);
		pTitle.setAlignment(Element.ALIGN_LEFT);

		Paragraph pAuthor = new Paragraph("Author: " + createdBy, FONT_AUTHOR);
		pAuthor.setAlignment(Element.ALIGN_LEFT);

		table.addCell(createTextCellWith2Paragraphs(pTitle, pAuthor, Element.ALIGN_CENTER));

		Paragraph pCreatedAt = new Paragraph(createdAt, FONT_DATE);
		pCreatedAt.setAlignment(Element.ALIGN_RIGHT);
		table.addCell(createTextCell(pCreatedAt, Element.ALIGN_TOP));

		document.add(table);

		document.add(addEmptyLines(1));

		Paragraph pContent = new Paragraph(content, FONT_CONTENT);
		pContent.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(pContent);

		document.close();

		InputStream in = new ByteArrayInputStream(out.toByteArray());

		manipulatePdf(in, out, document, FONT_PAGE_NUMBER);

		in = new ByteArrayInputStream(out.toByteArray());

		return in;
	}


	private void addMetaData(Document document, String title, String author, String tagList) {
		document.addTitle(title);
		document.addKeywords(tagList);
		document.addAuthor(author);
	}

	private static Paragraph addEmptyLines(int number) {
		Paragraph paragraph = new Paragraph();

		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}

		return paragraph;
	}

	public static PdfPCell createImageCell(String path) throws DocumentException, IOException {
		Image img = Image.getInstance(path);
		img.scaleToFit(50, 50);
		PdfPCell cell = new PdfPCell(img, true);
		cell.setBorder(Rectangle.NO_BORDER);
		return cell;
	}

	public static PdfPCell createTextCell(Paragraph p1, int alignment) throws DocumentException, IOException {
		PdfPCell cell = new PdfPCell();
		cell.addElement(p1);
		cell.setVerticalAlignment(alignment);
		cell.setBorder(Rectangle.NO_BORDER);
		return cell;
	}

	public static PdfPCell createTextCellWith2Paragraphs(Paragraph p1, Paragraph p2, int alignment)
			throws DocumentException, IOException {
		PdfPCell cell = new PdfPCell();
		cell.addElement(p1);
		cell.addElement(p2);
		cell.setVerticalAlignment(alignment);
		cell.setBorder(Rectangle.NO_BORDER);
		return cell;
	}

	public void manipulatePdf(InputStream in, OutputStream out, Document document, Font font)
			throws IOException, DocumentException {
		PdfReader reader = new PdfReader(in);
		int n = reader.getNumberOfPages();
		PdfStamper stamper = new PdfStamper(reader, out);
		PdfContentByte pagecontent;
		for (int i = 0; i < n;) {
			pagecontent = stamper.getOverContent(++i);
			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT,
					new Phrase(String.format("page %s of %s", i, n), font), document.right(), document.bottom() - 10,
					0);
		}
		stamper.close();
		reader.close();
	}

	public InputStream createExcel() throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HSSFWorkbook wb1 = new HSSFWorkbook();

		/* prepare the lists */
		List<Post> pList = postRepository.findAll();

		List<Long> ids = pList.stream().map(x -> x.getCreatedBy()).collect(Collectors.toList());
		Set<Users> aList = new HashSet<Users>();
		for (Long id : ids) {
			Optional<Users> u = userRepository.findById(id);
			aList.add(u.get());
		}

		List<Comment> cList = commentRepository.findAll();
		List<Long> ids2 = cList.stream().map(x -> x.getCreatedBy()).collect(Collectors.toList());
		Set<Users> rList = new HashSet<Users>();
		for (Long id : ids2) {
			Optional<Users> u = userRepository.findById(id);
			rList.add(u.get());
		}

		/* creation of the sheets */
		out = createSheets(wb1, pList, aList, rList, out);


		/* write values into output stream */
		try {
			wb1.write(out);
		} catch (IOException e) {
			System.out.println(e.toString());
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		}
		wb1.close();

		/* pass output stream as input stram */
		return new ByteArrayInputStream(out.toByteArray());
	}

	public ByteArrayOutputStream createSheets(HSSFWorkbook wb1, List<Post> pList, Set<Users> aList, Set<Users> rList,
			ByteArrayOutputStream out)
			throws IOException {

		// <------------------------------------------------------------POST------------------------------------------------------------------------------------------------->
		Sheet sheet = wb1.createSheet("Post");
		sheet = wb1.getSheetAt(0);

		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue((String) "Post");
		cell = row.createCell(1);
		cell.setCellValue((String) "Title");
		cell = row.createCell(2);
		cell.setCellValue((String) "is free");
		cell = row.createCell(3);
		cell.setCellValue((String) "Total view");
		cell = row.createCell(4);
		cell.setCellValue((String) "Unique View");
		cell = row.createCell(5);
		cell.setCellValue((String) "is banned");
		cell = row.createCell(6);
		cell.setCellValue((String) "AVG Rating");
		cell = row.createCell(7);
		cell.setCellValue((String) "number of purchases");

		int rowCount = 0;

		for (Post p : pList) {

			row = sheet.createRow(++rowCount);

			cell = row.createCell(0);
			cell.setCellValue((Long) p.getId());

			cell = row.createCell(1);
			cell.setCellValue((String) p.getTitle());

			cell = row.createCell(2);
			String YorN = "";
			YorN = (p.getCredit().getId() == (long) 5) ? "Y" : "N";
			cell.setCellValue((String) YorN);

			cell = row.createCell(3);
			List<PostViewed> pv = postViewdRepository.findByPost(p);
			cell.setCellValue((Integer) pv.size());

			cell = row.createCell(4);
			List<PostViewed> filteredByIp = StreamEx.of(pv).distinct(PostViewed::getIp).toList();
			cell.setCellValue((int) filteredByIp.size());

			cell = row.createCell(5);
			List<Blacklist> bl = blacklistRepository
					.findByUserAndIsVerifiedTrue(userRepository.findById(p.getCreatedBy()).get());
			String banned = "";
			banned = ((bl.isEmpty()) && (p.isVisible())) ? "N" : "Y";
			cell.setCellValue((String) banned);

			cell = row.createCell(6);
			if (p.getAvgRating() != null)
				cell.setCellValue((Double) p.getAvgRating());
			else
				cell.setCellValue((Double) 0.0);

			cell = row.createCell(7);
			// EXCEPT ROLE_READER, THE OTHER ROLES CAN VIEW THE POST
			int cnt = 0;
			if (p.getCredit().getId() != (long) 5) {
				List<Users> ulist = userRepository.findAll();
				for (Users u : ulist) {
					if (u.getRoles().size() < 2) {
						// CONTAINS ONLY ONE ROLE
						List<Long> id = u.getRoles().stream().map(x -> x.getId()).collect(Collectors.toList());
						if (id.get(0) == (long) 4) {
							Set<Post> postBoughtList = u.getPosts();
							if (postBoughtList.stream().filter(post -> (post.getId() == p.getId())).count() == 1)
								cnt++;
						}

					}
				}
			}
			cell.setCellValue((int) cnt);
		}

		// <------------------------------------------------------------AUTHOR------------------------------------------------------------------------------------------------->
		Sheet sheet2 = wb1.createSheet("Author");
		sheet2 = wb1.getSheetAt(1);

		row = sheet2.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue((String) "Author");
		cell = row.createCell(1);
		cell.setCellValue((String) "Total Posts");
		cell = row.createCell(2);
		cell.setCellValue((String) "Published Post");
		cell = row.createCell(3);
		cell.setCellValue((String) "Post to Check(isVisible=false)");
		cell = row.createCell(4);
		cell.setCellValue((String) "Banned Post");

		// FETCH THE VALUES DEPENDS ON EACH AUTHOR
		rowCount = 0;
		// int numTP, numPP, numPC, numBP = 0;
		int Psum[] = { 0, 0, 0, 0 };
		for (Users u : aList) {

			row = sheet2.createRow(++rowCount);

			cell = row.createCell(0);
			cell.setCellValue((String) u.getUsername());


			cell = row.createCell(1);
			List<Post> posts = postRepository.findByCreatedBy(u.getId());
			cell.setCellValue((int) posts.size());
			Psum[0] = Psum[0] + posts.size();

			cell = row.createCell(2);
			List<Post> vposts = postRepository.findByIsVisibleTrueAndCreatedBy(u.getId());
			cell.setCellValue((int) vposts.size());
			Psum[1] = Psum[1] + vposts.size();

			cell = row.createCell(3);
			cell.setCellValue((int) posts.size() - (int) vposts.size());
			Psum[2] = Psum[2] + posts.size() - vposts.size();

			cell = row.createCell(4);
			List<Blacklist> blist = blacklistRepository.findByUserAndIsVerifiedTrue(u);
			blist.stream().filter(x -> x.getPost().getId() != 0);
			cell.setCellValue((int) blist.size());
			Psum[3] = Psum[3] + blist.size();

		}

		row = sheet2.createRow(++rowCount);
		cell = row.createCell(0);
		cell.setCellValue((String) "Total");

		for (int i = 1; i < 5; i++) {
			cell = row.createCell(i);
			cell.setCellValue((int) Psum[i - 1]);
		}

		// <------------------------------------------------------------READER------------------------------------------------------------------------------------------------->
		Sheet sheet3 = wb1.createSheet("Reader");
		sheet3 = wb1.getSheetAt(2);

		row = sheet3.createRow(0);
		cell = row.createCell(0);
		cell.setCellValue((String) "Reader");
		cell = row.createCell(1);
		cell.setCellValue((String) "Total Comment");
		cell = row.createCell(2);
		cell.setCellValue((String) "Published Comment");
		cell = row.createCell(3);
		cell.setCellValue((String) "Comment to check");
		cell = row.createCell(4);
		cell.setCellValue((String) "Banned Comment");
		cell = row.createCell(5);
		cell.setCellValue((String) "Bought Post");
		cell = row.createCell(6);
		cell.setCellValue((String) "Credit");

		// FETCH THE VALUES DEPENDS ON EACH READER
		rowCount = 0;
		// int numTCom=0, numPCom=0, numComC=0, numBC=0, numBouP =0;
		int Csum[] = { 0, 0, 0, 0, 0 };
		for (Users u : rList) {

			row = sheet3.createRow(++rowCount);

			cell = row.createCell(0);
			cell.setCellValue((Long) u.getId());

			cell = row.createCell(1);
			List<Comment> comments = commentRepository.findByCreatedBy(u.getId());
			cell.setCellValue((int) comments.size());
			// numTCom = numTCom + comments.size();
			Csum[0] = Csum[0] + comments.size();

			cell = row.createCell(2);
			List<Comment> vcomments = commentRepository.findByIsVisibleTrueAndCreatedBy(u.getId());
			cell.setCellValue((int) vcomments.size());
			// numPCom = numPCom + vcomments.size();
			Csum[1] = Csum[1] + vcomments.size();
			
			cell = row.createCell(3);
			cell.setCellValue((int) comments.size() - vcomments.size());
			// numComC = numComC + comments.size() - vcomments.size();
			Csum[2] = Csum[2] + comments.size() - comments.size();
			
			cell = row.createCell(4);
			List<Blacklist> blist = blacklistRepository.findByUserAndIsVerifiedTrue(u);
			blist.stream().filter(x -> x.getPost().getId() == 0);
			cell.setCellValue((int) blist.size());
			// numBC = numBC + blist.size();
			Csum[3] = Csum[3] + blist.size();
			
			cell = row.createCell(5);
			Set<Post> posts = u.getPosts();
			cell.setCellValue((int) posts.size());
			// numBouP = numBouP + posts.size();
			Csum[4] = Csum[4] + posts.size();

			cell = row.createCell(6);
			cell.setCellValue((int) u.getCredit());

		}

		row = sheet3.createRow(++rowCount);
		cell = row.createCell(0);
		cell.setCellValue((String) "Total");
		for (int i = 1; i < 6; i++) {
			cell = row.createCell(i);
			cell.setCellValue((int) Csum[i - 1]);
		}

		return out;
	}

	/*
	 * impossible to do because of row and cell I must use same one here but
	 * difficult to pass it public ByteArrayOutputStream
	 * createAuthorSheet(HSSFWorkbook wb1, List<Post> listname,
	 * ByteArrayOutputStream out, int num) throws IOException { // TODO
	 * Auto-generated method stub Sheet sheet2 = wb1.createSheet("Author");
	 * 
	 * sheet2 = wb1.getSheetAt(num); Row row = sheet2.createRow(0); Cell cell =
	 * row.createCell(0); cell.setCellValue((String) "Author"); cell =
	 * row.createCell(1); cell.setCellValue((String) "Total Posts"); cell =
	 * row.createCell(2); cell.setCellValue((String) "Published Post"); cell =
	 * row.createCell(3); cell.setCellValue((String)
	 * "Post to Check(isVisible=false)"); cell = row.createCell(4);
	 * cell.setCellValue((String) "Banned Post");
	 * 
	 * //for operation to fetch values depends on each author
	 * 
	 * return out; }
	 */

}