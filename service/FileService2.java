package it.course.myblog.service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.ByteArrayInputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
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
import it.course.myblog.repository.BlacklistRepository;
import it.course.myblog.repository.CommentRepository;
import it.course.myblog.repository.PostRepository;
import it.course.myblog.repository.PostViewedRepository;
import it.course.myblog.repository.RoleRepository;
import it.course.myblog.repository.UserRepository;
import one.util.streamex.StreamEx;

@Service
public class FileService2 {
	
	@Autowired
	PostRepository postRepository;
	
	@Autowired
	PostViewedRepository postViewedRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	CommentRepository commentRepository;
	
	@Autowired
	BlacklistRepository blacklistRepository;
	
	//attributi PDF
	private static Font FONT_TITLE  = new Font(Font.FontFamily.TIMES_ROMAN, 16,  Font.BOLD);
	private static Font FONT_CONTENT  = new Font(Font.FontFamily.TIMES_ROMAN, 12,  Font.NORMAL);
	private static Font FONT_AUTHOR  = new Font(Font.FontFamily.TIMES_ROMAN, 12,  Font.ITALIC);
	private static Font FONT_DATE  = new Font(Font.FontFamily.TIMES_ROMAN, 10,  Font.ITALIC, BaseColor.LIGHT_GRAY);
	private static Font FONT_PAGE_NUMBER  = new Font(Font.FontFamily.TIMES_ROMAN, 9,  Font.ITALIC, BaseColor.LIGHT_GRAY);
	
	private static Font smallBold  = new Font(Font.FontFamily.TIMES_ROMAN, 12,  Font.BOLD);
	
	//inizio metodo generazione PDF
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
		
		addMetaData(document, title, createdBy , tagList.toString());
		
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		addCustomRows(table);
		addRows(table, title);
		
		document.add(table);
		
		Paragraph pCreatedAt = new Paragraph(createdAt, FONT_DATE);
		pCreatedAt.setAlignment(Element.ALIGN_RIGHT);
		document.add(pCreatedAt);
		
		/*
		Paragraph pTitle = new Paragraph(title, FONT_TITLE);
		pTitle.setAlignment(Element.ALIGN_LEFT);
		document.add(pTitle);
		*/
		
		Paragraph pAuthor = new Paragraph("Author: "+createdBy, FONT_AUTHOR);
		pAuthor.setAlignment(Element.ALIGN_LEFT);
		addEmptyLine(pAuthor, 1);
		document.add(pAuthor);
			
		Paragraph pContent = new Paragraph(content, FONT_CONTENT);
		pContent.setAlignment(Element.ALIGN_JUSTIFIED);
		document.add(pContent);
		
		document.close();
		
		InputStream in = new ByteArrayInputStream(out.toByteArray());
		
		//pagination
		PdfReader reader = new PdfReader(in);
        int n = reader.getNumberOfPages();
        PdfStamper stamper = new PdfStamper(reader, out);
        PdfContentByte pagecontent;
        
        for (int i = 0; i < n;) {
        	pagecontent = stamper.getUnderContent(++i);
        	ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s of %s", i, n), FONT_PAGE_NUMBER), document.right(), document.bottom()-10, 0);
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
	
	//fine metodo generazione PDF
	
	//inizio metodo generazione XLS
	public InputStream createXls (List<Users> us, List<Post> ps) throws IOException{
		 
		HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheetAuthor = workbook.createSheet("Author Report");
        sheetAuthor.setDefaultColumnWidth(20);
        HSSFSheet sheetReader = workbook.createSheet("Reader Report");
        sheetReader.setDefaultColumnWidth(30);
        HSSFSheet sheetPost = workbook.createSheet("Post Report");
        sheetPost.setDefaultColumnWidth(20);
        
        //Author Report Sheet
        
        createAuthorHeaderRow(sheetAuthor);
 
        int authRowCount = 0;
        
        for (Users a : us) {
        	
        	Set<Role> roles = a.getRoles().stream().filter(u -> u.getName().equals(RoleName.ROLE_EDITOR)).collect(Collectors.toSet());
    		Optional<Users> editor = userRepository.findByIdAndRolesIn(a.getId(), roles);
    		
    		if(editor.isPresent() || a.getPosts().size()>0) {
    			Row row = sheetAuthor.createRow(++authRowCount);
                writeAuthor(a, row, sheetAuthor);
    		}
            
        }
        
        createAuthorFooterRow(sheetAuthor, authRowCount);
        
        //Reader Report Sheet
        
        createReaderHeaderRow(sheetReader);
        
        int readRowCount = 0;
        
        for (Users r : us) {
        	
        	Set<Role> roles = r.getRoles().stream().filter(u -> u.getName().equals(RoleName.ROLE_READER)).collect(Collectors.toSet());
    		Optional<Users> reader = userRepository.findByIdAndRolesIn(r.getId(), roles);
    		
    		List<Comment> com = commentRepository.findByIsVisibleTrueAndCreatedBy(r.getId());
    		
    		if(reader.isPresent() || com.size()>0) {
    			Row row = sheetReader.createRow(++readRowCount);
                writeReader(r, row, sheetReader);
    		}
            
        }
        
        createReaderFooterRow(sheetReader, readRowCount);
        
        //Post Report Sheet
        
        createPostHeaderRow(sheetPost);
        
        int postRowCount = 0;
        
        for (Post p : ps) {
            Row row = sheetPost.createRow(++postRowCount);
            writePost(p, row, sheetPost);  
        }
        
        createPostFooterRow(sheetPost, postRowCount);
        
        
        //OUTPUT
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
       
        workbook.write(out);
        workbook.close();
        
        out.close();
        
        InputStream in = new ByteArrayInputStream(out.toByteArray());
        
        return in;
		
	}
	
	//AUTHOR
	
	private void createAuthorHeaderRow(Sheet sheet) {
		
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 16);
		cellStyle.setFont(font);
				
		Row row = sheet.createRow(0);
		Cell cellAuthor = row.createCell(1);
		cellAuthor.setCellStyle(cellStyle);
		cellAuthor.setCellValue("Author");
		
		Cell cellTotalPosts = row.createCell(2);
		cellTotalPosts.setCellStyle(cellStyle);
		cellTotalPosts.setCellValue("Total Posts");
		
		Cell cellPublPosts = row.createCell(3);
		cellPublPosts.setCellStyle(cellStyle);
		cellPublPosts.setCellValue("Published Posts");
		
		Cell cellUncheckPosts = row.createCell(4);
		cellUncheckPosts.setCellStyle(cellStyle);
		cellUncheckPosts.setCellValue("Posts to check");
		
		Cell cellBanPosts = row.createCell(5);
		cellBanPosts.setCellStyle(cellStyle);
		cellBanPosts.setCellValue("Banned Posts");
	}
	
	private void writeAuthor(Users u, Row row, Sheet sheet) {
		
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
		font.setFontHeightInPoints((short) 14);
		cellStyle.setFont(font);
		
		long allPosts = postRepository.countByCreatedBy(u.getId());
		List<Post> publishedPosts = postRepository.findByIsVisibleTrueAndCreatedBy(u.getId());
		List<Post> uncheckedPosts = postRepository.findByIsApprovedFalseAndCreatedBy(u.getId());
		List<Post> bans = new ArrayList<Post>();
		for(Post p: uncheckedPosts) {
			if(p.getBlacklists().size()>0) {
				bans.add(p);
				uncheckedPosts.remove(p); //se il post è bannato, non compare nella lista degli unchecked
			}		
		}
		
	    Cell cell = row.createCell(1);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(u.getUsername());
	 
	    cell = row.createCell(2);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(allPosts);
	    
	    cell = row.createCell(3);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(publishedPosts.size());
	    
	    cell = row.createCell(4);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(uncheckedPosts.size());
	    
	    cell = row.createCell(5);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(bans.size());
	    
	}
	
	private void createAuthorFooterRow(Sheet sheet, int rowCount) {
		
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 14);
		cellStyle.setFont(font);
	    
	    Row rowTotal = sheet.createRow(rowCount + 2);
	    Cell cellTotalText = rowTotal.createCell(1);
	    cellTotalText.setCellStyle(cellStyle);
	    cellTotalText.setCellValue("Total:");
	    
	    Cell cellTotal = rowTotal.createCell(2);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(C2:C" + (rowCount + 2) + ")");
	    
	    cellTotal = rowTotal.createCell(3);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(D2:D" + (rowCount + 2) + ")");
	    
	    cellTotal = rowTotal.createCell(4);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(E2:E" + (rowCount + 2) + ")");
	    
	    cellTotal = rowTotal.createCell(5);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(F2:F" + (rowCount + 2) + ")");
    
	}
	
	//READER
	
	private void createReaderHeaderRow(Sheet sheet) {
		
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 16);
		cellStyle.setFont(font);
				
		Row row = sheet.createRow(0);
		Cell cellReader = row.createCell(1);
		cellReader.setCellStyle(cellStyle);
		cellReader.setCellValue("Reader");
		
		Cell cellTotalComments = row.createCell(2);
		cellTotalComments.setCellStyle(cellStyle);
		cellTotalComments.setCellValue("Total Comments");
		
		Cell cellPublComments = row.createCell(3);
		cellPublComments.setCellStyle(cellStyle);
		cellPublComments.setCellValue("Published Comments");
		
		Cell cellUncheckComments = row.createCell(4);
		cellUncheckComments.setCellStyle(cellStyle);
		cellUncheckComments.setCellValue("Comments to check");
		
		Cell cellBanComments = row.createCell(5);
		cellBanComments.setCellStyle(cellStyle);
		cellBanComments.setCellValue("Banned Comments");
		
		Cell cellBoughtPosts = row.createCell(6);
		cellBoughtPosts.setCellStyle(cellStyle);
		cellBoughtPosts.setCellValue("Bought Posts");
		
		Cell cellReaderCredits = row.createCell(7);
		cellReaderCredits.setCellStyle(cellStyle);
		cellReaderCredits.setCellValue("Reader Credits");
	}
	
	private void writeReader(Users u, Row row, Sheet sheet) {
		
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
		font.setFontHeightInPoints((short) 14);
		cellStyle.setFont(font);
		
		long allComments = commentRepository.countByCreatedBy(u.getId());
		List<Comment> publishedComments = commentRepository.findByIsVisibleTrueAndCreatedBy(u.getId());
		List<Comment> uncheckedComments = commentRepository.findByIsVisibleFalseAndCreatedBy(u.getId());
		List<Comment> bans = new ArrayList<Comment>();
		List<Blacklist> blk = blacklistRepository.findAllByUserId(u.getId());
		for(Comment c: uncheckedComments) {
			List<Blacklist> blkCom = blk.stream().filter(b -> (b.getCommentId() == c.getId())).collect(Collectors.toList());
			if(blkCom.size()>0) {
				bans.add(c);
				uncheckedComments.remove(c); //se il commento è bannato, non compare nella lista degli unchecked
			}		
		}
		
	    Cell cell = row.createCell(1);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(u.getUsername());
	 
	    cell = row.createCell(2);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(allComments);
	    
	    cell = row.createCell(3);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(publishedComments.size());
	    
	    cell = row.createCell(4);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(uncheckedComments.size());
	    
	    cell = row.createCell(5);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(bans.size());
	    
	    cell = row.createCell(6);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(u.getPosts().size());
	    
	    cell = row.createCell(7);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(u.getCredit());
	}
	
	private void createReaderFooterRow(Sheet sheet, int rowCount) {
		
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 14);
		cellStyle.setFont(font);
	    
	    Row rowTotal = sheet.createRow(rowCount + 2);
	    Cell cellTotalText = rowTotal.createCell(1);
	    cellTotalText.setCellStyle(cellStyle);
	    cellTotalText.setCellValue("Total:");
	    
	    Cell cellTotal = rowTotal.createCell(2);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(C2:C" + (rowCount + 2) + ")");
	    
	    cellTotal = rowTotal.createCell(3);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(D2:D" + (rowCount + 2) + ")");
	    
	    cellTotal = rowTotal.createCell(4);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(E2:E" + (rowCount + 2) + ")");
	    
	    cellTotal = rowTotal.createCell(5);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(F2:F" + (rowCount + 2) + ")");
	    
	    cellTotal = rowTotal.createCell(6);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(G2:G" + (rowCount + 2) + ")");
	    
	    cellTotal = rowTotal.createCell(7);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(H2:H" + (rowCount + 2) + ")");
    
	}
	
	//POST
	
	private void createPostHeaderRow(Sheet sheet) {
		
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 16);
		cellStyle.setFont(font);
				
		Row row = sheet.createRow(0);
		Cell cellPostTitle = row.createCell(1);
		cellPostTitle.setCellStyle(cellStyle);
		cellPostTitle.setCellValue("Post Title");
		
		Cell cellPostID = row.createCell(2);
		cellPostID.setCellStyle(cellStyle);
		cellPostID.setCellValue("Post ID");
		
		Cell cellPostFree = row.createCell(3);
		cellPostFree.setCellStyle(cellStyle);
		cellPostFree.setCellValue("Is Free");
		
		Cell cellPostTotViews = row.createCell(4);
		cellPostTotViews.setCellStyle(cellStyle);
		cellPostTotViews.setCellValue("Total Views");
		
		Cell cellPostUniQViews = row.createCell(5);
		cellPostUniQViews.setCellStyle(cellStyle);
		cellPostUniQViews.setCellValue("Unique Views");
		
		Cell cellPostBan = row.createCell(6);
		cellPostBan.setCellStyle(cellStyle);
		cellPostBan.setCellValue("Is Banned");
		
		Cell cellNumPurchases = row.createCell(7);
		cellNumPurchases.setCellStyle(cellStyle);
		cellNumPurchases.setCellValue("Number of Purchases");
		
		Cell cellAVGRating = row.createCell(8);
		cellAVGRating.setCellStyle(cellStyle);
		cellAVGRating.setCellValue("AVG Rating");
	}
	
	private void writePost(Post p, Row row, Sheet sheet) {
		
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
		font.setFontHeightInPoints((short) 14);
		cellStyle.setFont(font);
		
		int free = 0;
		if(p.getCredit().getCreditImport()<1) {
			free = 1;
		}
		List<PostViewed> totalViews = postViewedRepository.findByPost(p);
		List<PostViewed> uniquesViews = StreamEx.of(totalViews).distinct(PostViewed::getIp).toList();
		int banned = 0;
		if(p.getBlacklists().size()>0) {
			banned = 1;
		}
		
		
		Cell cell = row.createCell(1);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(p.getTitle());
	    
	    cell = row.createCell(2);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(p.getId());
	    
	    cell = row.createCell(3);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(free);
	    
	    cell = row.createCell(4);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(totalViews.size());
	    
	    cell = row.createCell(5);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(uniquesViews.size());
	    
	    cell = row.createCell(6);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(banned);
	    
	    cell = row.createCell(7);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(p.getUsersWhoBought().size());
	    
	    cell = row.createCell(8);
	    cell.setCellStyle(cellStyle);
	    cell.setCellValue(p.getAvgRating());
	    
	}
	
	private void createPostFooterRow(Sheet sheet, int rowCount) {
		
		CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		org.apache.poi.ss.usermodel.Font font = sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setFontHeightInPoints((short) 14);
		cellStyle.setFont(font);
		
		List<Post> posts = postRepository.findAll();
				
		Row rowTotal = sheet.createRow(rowCount + 2);
	    Cell cellTotalText = rowTotal.createCell(1);
	    cellTotalText.setCellStyle(cellStyle);
	    cellTotalText.setCellValue("Total:");
	    
	    Cell cellTotal = rowTotal.createCell(2);
	    cellTotal.setCellStyle(cellStyle);
	    //cellTotal.setCellFormula("SUM(C2:C" + (rowCount + 2) + ")");
	    cellTotal.setCellValue(posts.size());
	    
	    cellTotal = rowTotal.createCell(3);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(D2:D" + (rowCount + 2) + ")");
	    
	    cellTotal = rowTotal.createCell(4);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(E2:E" + (rowCount + 2) + ")");
	    
	    cellTotal = rowTotal.createCell(5);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(F2:F" + (rowCount + 2) + ")");
	    
	    cellTotal = rowTotal.createCell(6);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(G2:G" + (rowCount + 2) + ")");
	    
	    cellTotal = rowTotal.createCell(7);
	    cellTotal.setCellStyle(cellStyle);
	    cellTotal.setCellFormula("SUM(H2:H" + (rowCount + 2) + ")");
	}
	
}
