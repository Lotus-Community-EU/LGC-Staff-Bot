//Created by Maurice H. at 14.01.2025
package eu.lotusgaming.bot.misc;

public class CustomCommand {
	
	private String name;
	private ReplyAs replyAs;
	private String content;
	private EmbedData embedData;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ReplyAs getReplyAs() {
		return replyAs;
	}

	public void setReplyAs(ReplyAs replyAs) {
		this.replyAs = replyAs;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public EmbedData getEmbedData() {
		return embedData;
	}

	public void setEmbedData(EmbedData embedData) {
		this.embedData = embedData;
	}

	public static class EmbedData {
		private String description;
		private String color;
		private String title;
		private Author author;
		private Footer footer;
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getColor() {
			return color;
		}
		public void setColor(String color) {
			this.color = color;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public Author getAuthor() {
			return author;
		}
		public void setAuthor(Author author) {
			this.author = author;
		}
		public Footer getFooter() {
			return footer;
		}
		public void setFooter(Footer footer) {
			this.footer = footer;
		}
	}
	
	public static class Author {
		private String avatar;
		private String name;
		
		public String getAvatar() {
			return avatar;
		}
		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static class Footer {
		private String icon;
		private String text;
		
		public String getIcon() {
			return icon;
		}
		public void setIcon(String icon) {
			this.icon = icon;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
	}
	
	public static enum ReplyAs {
		TEXT, EMBED
	}

}