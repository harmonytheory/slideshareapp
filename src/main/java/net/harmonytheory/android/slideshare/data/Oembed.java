package net.harmonytheory.android.slideshare.data;

import net.harmonytheory.apt.annotation.Column;
import net.harmonytheory.apt.annotation.SqliteBean;
import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;
import android.os.Parcel;
import android.os.Parcelable;

@SqliteBean(helper="net.harmonytheory.android.slideshare.db.SlideShareDatabaseHelper")
@JsonModel(decamelize = true)
public class Oembed implements Parcelable {
	public Oembed() {
	}
	@Column
	@JsonKey
	private String providerUrl;
	@Column
	@JsonKey
	private String type;
	@Column
	@JsonKey
	private String slideImageBaseurl;
	@Column
	@JsonKey
	private int thumbnailWidth;
	@Column
	@JsonKey
	private String thumbnail;
	@Column
	@JsonKey
	private int conversionVersion;
	@Column
	@JsonKey
	private int thumbnailHeight;
	@Column
	@JsonKey
	private String version;
	@Column
	@JsonKey
	private String versionNo;
	@Column
	@JsonKey
	private int width;
	@Column
	@JsonKey
	private String html;
	@Column
	@JsonKey
	private String authorName;
	@Column
	@JsonKey
	private String slideImageBaseurlSuffix;
	@Column
	@JsonKey
	private int totalSlides;
	@Column
	@JsonKey
	private String authorUrl;
	@Column
	@JsonKey
	private String title;
	@Column
	@JsonKey
	private int height;
	@Column
	@JsonKey
	private String providerName;
	@Column(primary=true)
	@JsonKey
	private int slideshowId;

	public String getProviderUrl() {
		return providerUrl;
	}
	public void setProviderUrl(String providerUrl) {
		this.providerUrl = providerUrl;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSlideImageBaseurl() {
		return slideImageBaseurl;
	}
	public void setSlideImageBaseurl(String slideImageBaseurl) {
		this.slideImageBaseurl = slideImageBaseurl;
	}
	public int getThumbnailWidth() {
		return thumbnailWidth;
	}
	public void setThumbnailWidth(int thumbnailWidth) {
		this.thumbnailWidth = thumbnailWidth;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public int getConversionVersion() {
		return conversionVersion;
	}
	public void setConversionVersion(int conversionVersion) {
		this.conversionVersion = conversionVersion;
	}
	public int getThumbnailHeight() {
		return thumbnailHeight;
	}
	public void setThumbnailHeight(int thumbnailHeight) {
		this.thumbnailHeight = thumbnailHeight;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getVersionNo() {
		return versionNo;
	}
	public void setVersionNo(String versionNo) {
		this.versionNo = versionNo;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public String getHtml() {
		return html;
	}
	public void setHtml(String html) {
		this.html = html;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}
	public String getSlideImageBaseurlSuffix() {
		return slideImageBaseurlSuffix;
	}
	public void setSlideImageBaseurlSuffix(String slideImageBaseurlSuffix) {
		this.slideImageBaseurlSuffix = slideImageBaseurlSuffix;
	}
	public int getTotalSlides() {
		return totalSlides;
	}
	public void setTotalSlides(int totalSlides) {
		this.totalSlides = totalSlides;
	}
	public String getAuthorUrl() {
		return authorUrl;
	}
	public void setAuthorUrl(String authorUrl) {
		this.authorUrl = authorUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public int getSlideshowId() {
		return slideshowId;
	}
	public void setSlideshowId(int slideshowId) {
		this.slideshowId = slideshowId;
	}
	public String getSlideImageUrl(int slideNo) {
		return new StringBuilder("http:").append(getSlideImageBaseurl()).append(slideNo).append(getSlideImageBaseurlSuffix()).toString();
	}
	public String getExt() {
		return getSlideImageBaseurlSuffix().substring(getSlideImageBaseurlSuffix().lastIndexOf("."));
	}

	public static final Parcelable.Creator<Oembed> CREATOR = new Parcelable.Creator<Oembed>() {
		public Oembed createFromParcel(Parcel in) {
			return new Oembed(in);
		}
		public Oembed[] newArray(int size) {
			return new Oembed[size];
		}
	};

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		out.writeString(providerUrl);
		out.writeString(type);
		out.writeString(slideImageBaseurl);
		out.writeInt(thumbnailWidth);
		out.writeString(thumbnail);
		out.writeInt(conversionVersion);
		out.writeInt(thumbnailHeight);
		out.writeString(version);
		out.writeString(versionNo);
		out.writeInt(width);
		out.writeString(html);
		out.writeString(authorName);
		out.writeString(slideImageBaseurlSuffix);
		out.writeInt(totalSlides);
		out.writeString(authorUrl);
		out.writeString(title);
		out.writeInt(height);
		out.writeString(providerName);
		out.writeInt(slideshowId);
	}

	private Oembed(Parcel in) {
		providerUrl = in.readString();
		type = in.readString();
		slideImageBaseurl = in.readString();
		thumbnailWidth = in.readInt();
		thumbnail = in.readString();
		conversionVersion = in.readInt();
		thumbnailHeight = in.readInt();
		version = in.readString();
		versionNo = in.readString();
		width = in.readInt();
		html = in.readString();
		authorName = in.readString();
		slideImageBaseurlSuffix = in.readString();
		totalSlides = in.readInt();
		authorUrl = in.readString();
		title = in.readString();
		height = in.readInt();
		providerName = in.readString();
		slideshowId = in.readInt();
	}
}
