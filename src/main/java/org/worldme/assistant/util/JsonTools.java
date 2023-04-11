package org.worldme.assistant.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * @author 戴着假发的程序员
 * 
 * @company 起点编程
 *
 * 2022年8月18日 下午1:43:24
 */
public class JsonTools {
	private static ObjectMapper mapper = new ObjectMapper();
	// 将对象转换为JSON
	public static String object2json(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	// 将JSON转换对应的对象
	public static <T> T json2object(String json,Class<T> clazz) {
		try {
			return mapper.readValue(json, clazz);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 将json转换为集合
	public static <T> List<T> json2List(String json, Class<T> clazz){
		try {
			return mapper.readValue(json,collectionType(List.class,clazz));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static JavaType collectionType(Class<?> collectionClz, Class<?> ...elementClz) {
		return mapper.getTypeFactory().constructParametricType(collectionClz, elementClz);
	}
}
