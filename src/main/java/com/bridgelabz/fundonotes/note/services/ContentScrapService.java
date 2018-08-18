package com.bridgelabz.fundonotes.note.services;

import java.util.List;

import com.bridgelabz.fundonotes.note.exception.MalFormedException;
import com.bridgelabz.fundonotes.note.model.UrlMetaData;

public interface ContentScrapService {
	
	public List<UrlMetaData> addContent(String url) throws MalFormedException;
    public List<UrlMetaData> addSplitContent(String description) throws MalFormedException;
}
