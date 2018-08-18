package com.bridgelabz.fundonotes.note.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.bridgelabz.fundonotes.note.exception.MalFormedException;
import com.bridgelabz.fundonotes.note.model.UrlMetaData;

@Service
public class ContentScrapServiceImpl implements ContentScrapService {

	@Autowired
	private Environment environment;

	@Override
	public List<UrlMetaData> addContent(String url) throws MalFormedException {

		List<UrlMetaData> urlList = new ArrayList<>();

		Document doc;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			throw new MalFormedException(environment.getProperty("MalFormedException"));
		}

		String keywords = doc.select(environment.getProperty("KEYWORDS")).first()
				.attr(environment.getProperty("CONTENT"));
		String description = doc.select(environment.getProperty("DESCRIPTION")).get(0)
				.attr(environment.getProperty("CONTENT"));
		Elements images = doc.select(environment.getProperty("IMAGES"));
		UrlMetaData metaData = new UrlMetaData();
		metaData.setUrl(url);
		metaData.setKeywords(keywords);
		metaData.setDescription(description);
		for (Element image : images) {
			metaData.setImageUrl(image.absUrl("src"));
		}
		urlList.add(metaData);
		return urlList;
	}

	@Override
	public List<UrlMetaData> addSplitContent(String description) throws MalFormedException {
		String[] contents = description.split(" ");
		if (contents.length > 0) {
			for (int i = 0; i < contents.length; i++) {
				if (contents[i].startsWith("http://") || contents[i].startsWith("https://")) {
					UrlValidator validator = new UrlValidator();
					if (validator.isValid(contents[i]))
						;
					List<UrlMetaData> metaData = addContent(contents[i]);
					
					return metaData;
				}
			}
		}
		return null;
	}
}
