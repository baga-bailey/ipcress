package uk.co.mafew.ipcress.nephele.aws;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3 {

	public static void main(String[] args) {

		S3 awsClient = new S3();
		String region = "";
		String accesskey = "";
		String secretkey = "";
		String bucket = "";
		// This is a test

		// awsClient.listObjects(region, bucket);
		// awsClient.deleteObjects(accesskey, secretkey, region, bucket,
		// "JBTest2/hello.txt");
		// awsClient.downloadObject(accesskey, secretkey, region, bucket,
		// "JBTesting/password.txt", "C:\\Users\\jonat\\Documents\\downloadedDoc.txt");

	}

	/*
	 * 
	 * List Objects
	 * 
	 */

	public Node listObjects(String accessKey, String secretKey, String region, String bucketName) {
		return listObjects(accessKey, secretKey, region, bucketName, ".*", false);
	}

	public Node listObjects_IAM(String region, String bucketName) {
		return listObjects_IAM(region, bucketName, ".*", false);
	}

	public Node listObjectsFiltered(String accessKey, String secretKey, String region, String bucketName,
			String filter) {
		return listObjects(accessKey, secretKey, region, bucketName, filter, false);
	}

	public Node listObjectsWithProperties(String accessKey, String secretKey, String region, String bucketName) {
		return listObjects(accessKey, secretKey, region, bucketName, ".*", true);
	}

	public Node listObjectsWithPropertiesFiltered(String accessKey, String secretKey, String region, String bucketName,
			String filter) {
		return listObjects(accessKey, secretKey, region, bucketName, filter, true);
	}

	public Node listObjects(String bucketName, String regex, boolean verbose) {
		Node result = null;
		Document doc = null;
		Regions reg = null;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<objects></objects>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			/*
			 * reg = Regions.fromName(region); if (reg == null) { Node node =
			 * doc.createElement("error"); node.setTextContent("Invalid region name: " +
			 * region); doc.getElementsByTagName("objects").item(0).appendChild(node); }
			 */

			// AmazonS3 s3client = AmazonS3ClientBuilder.standard()
			// .withCredentials(new
			// AWSStaticCredentialsProvider(credentials)).withRegion(reg).build()
			// ;
			AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
			/*
			 * I've left in the 'WithPrefix' for future reference
			 */
			ListObjectsRequest lor = new ListObjectsRequest().withBucketName(bucketName).withPrefix("");

			ObjectListing objectListing = s3client.listObjects(lor);
			for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
				if (os.getKey().matches(regex)) {
					Node objectNode = doc.createElement("object");
					Node keyNode = doc.createElement("key");
					keyNode.setTextContent(os.getKey());
					objectNode.appendChild(keyNode);
					System.out.println("key = " + os.getKey());
					if (verbose) {
						Node bucketNameNode = doc.createElement("bucketNameNode");
						bucketNameNode.setTextContent(os.getBucketName());
						objectNode.appendChild(bucketNameNode);
						System.out.println("Bucket name = " + os.getBucketName());

						Node ETagNode = doc.createElement("ETag");
						ETagNode.setTextContent(os.getETag());
						objectNode.appendChild(ETagNode);
						System.out.println("ETag = " + os.getETag());

						Node sizeNode = doc.createElement("size");
						sizeNode.setTextContent(String.valueOf(os.getSize()));
						objectNode.appendChild(sizeNode);
						System.out.println("Size = " + String.valueOf(os.getSize()));

						Node storageClassNode = doc.createElement("storageClass");
						storageClassNode.setTextContent(os.getStorageClass());
						objectNode.appendChild(storageClassNode);
						System.out.println("Storage Class = " + os.getStorageClass());

						Node lastModifiedNode = doc.createElement("lastModified");
						lastModifiedNode.setTextContent(os.getLastModified().toString());
						objectNode.appendChild(lastModifiedNode);
						System.out.println("Last Modified = " + os.getLastModified().toString());

						Node ownerNode = doc.createElement("owner");
						Node idNode = doc.createElement("id");
						idNode.setTextContent(os.getOwner().getId());
						ownerNode.appendChild(idNode);
						Node displayNameNode = doc.createElement("displayName");
						displayNameNode.setTextContent(os.getOwner().getDisplayName());
						ownerNode.appendChild(displayNameNode);
						objectNode.appendChild(ownerNode);
						System.out.println("Owner = " + os.getOwner());
					}
					doc.getElementsByTagName("objects").item(0).appendChild(objectNode);
				}
			}

		} catch (Exception e) {
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("objects").item(0).appendChild(node);
		}

		result = doc.getElementsByTagName("objects").item(0);
		return result;
	}

	

	public Node listObjects_IAM(String region, String bucketName, String regex, boolean verbose) {
		Node result = null;
		Document doc = null;
		Regions reg = null;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<objects></objects>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			reg = Regions.fromName(region);
			if (reg == null) {
				Node node = doc.createElement("error");
				node.setTextContent("Invalid region name: " + region);
				doc.getElementsByTagName("objects").item(0).appendChild(node);
			}

			AmazonS3 s3client = AmazonS3ClientBuilder.standard().withRegion(reg).build();
			/*
			 * I've left in the 'WithPrefix' for future reference
			 */
			ListObjectsRequest lor = new ListObjectsRequest().withBucketName(bucketName).withPrefix("");

			ObjectListing objectListing = s3client.listObjects(lor);
			for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
				if (os.getKey().matches(regex)) {
					Node objectNode = doc.createElement("object");
					Node keyNode = doc.createElement("key");
					keyNode.setTextContent(os.getKey());
					objectNode.appendChild(keyNode);
					System.out.println("key = " + os.getKey());
					if (verbose) {
						Node bucketNameNode = doc.createElement("bucketNameNode");
						bucketNameNode.setTextContent(os.getBucketName());
						objectNode.appendChild(bucketNameNode);
						System.out.println("Bucket name = " + os.getBucketName());

						Node ETagNode = doc.createElement("ETag");
						ETagNode.setTextContent(os.getETag());
						objectNode.appendChild(ETagNode);
						System.out.println("ETag = " + os.getETag());

						Node sizeNode = doc.createElement("size");
						sizeNode.setTextContent(String.valueOf(os.getSize()));
						objectNode.appendChild(sizeNode);
						System.out.println("Size = " + String.valueOf(os.getSize()));

						Node storageClassNode = doc.createElement("storageClass");
						storageClassNode.setTextContent(os.getStorageClass());
						objectNode.appendChild(storageClassNode);
						System.out.println("Storage Class = " + os.getStorageClass());

						Node lastModifiedNode = doc.createElement("lastModified");
						lastModifiedNode.setTextContent(os.getLastModified().toString());
						objectNode.appendChild(lastModifiedNode);
						System.out.println("Last Modified = " + os.getLastModified().toString());

						Node ownerNode = doc.createElement("owner");
						Node idNode = doc.createElement("id");
						idNode.setTextContent(os.getOwner().getId());
						ownerNode.appendChild(idNode);
						Node displayNameNode = doc.createElement("displayName");
						displayNameNode.setTextContent(os.getOwner().getDisplayName());
						ownerNode.appendChild(displayNameNode);
						objectNode.appendChild(ownerNode);
						System.out.println("Owner = " + os.getOwner());
					}
					doc.getElementsByTagName("objects").item(0).appendChild(objectNode);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("objects").item(0).appendChild(node);
		}

		result = doc.getElementsByTagName("objects").item(0);
		return result;
	}

	/*
	 * Delete Object
	 */
	public void deleteObject(String accessKey, String secretKey, String region, String bucketName, String objectName)
			throws Exception {

		Regions reg = null;

		reg = Regions.fromName(region);

		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(reg).build();
		DeleteObjectRequest dor = new DeleteObjectRequest(bucketName, objectName);

		s3client.deleteObject(dor);
	}

	public Node uploadObject(String accessKey, String secretKey, String region, String bucketName, String fullFilePath,
			String key) {
		Node result = null;
		Document doc = null;
		Regions reg = null;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<objects></objects>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			reg = Regions.fromName(region);
			if (reg == null) {
				Node node = doc.createElement("error");
				node.setTextContent("Invalid region name: " + region);
				doc.getElementsByTagName("objects").item(0).appendChild(node);
			}

			AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

			AmazonS3 s3client = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(reg).build();
			File file = new File(fullFilePath);
			PutObjectRequest por = new PutObjectRequest(bucketName, key, file);

			s3client.putObject(por);

			Node node = doc.createElement("result");
			node.setTextContent("success");
			doc.getElementsByTagName("objects").item(0).appendChild(node);

		} catch (Exception e) {
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("objects").item(0).appendChild(node);
		}

		result = doc.getElementsByTagName("objects").item(0);
		return result;
	}

	public Node downloadObject(String accessKey, String secretKey, String region, String bucketName, String key,
			String destination) {
		Node result = null;
		Document doc = null;
		Regions reg = null;
		OutputStream outStream = null;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<objects></objects>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			reg = Regions.fromName(region);
			if (reg == null) {
				Node node = doc.createElement("error");
				node.setTextContent("Invalid region name: " + region);
				doc.getElementsByTagName("objects").item(0).appendChild(node);
			}

			AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

			AmazonS3 s3client = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(reg).build();

			GetObjectRequest gor = new GetObjectRequest(bucketName, key);

			S3Object s3object = s3client.getObject(gor);
			S3ObjectInputStream inputStream = s3object.getObjectContent();
			byte[] buffer = new byte[inputStream.available()];
			inputStream.read(buffer);

			File targetFile = new File(destination);
			outStream = new FileOutputStream(targetFile);
			outStream.write(buffer);

			Node node = doc.createElement("result");
			node.setTextContent("success");
			doc.getElementsByTagName("objects").item(0).appendChild(node);

		} catch (Exception e) {
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("objects").item(0).appendChild(node);
		} finally {
			try {
				outStream.flush();
				outStream.close();
			} catch (Exception e) {
			}
		}

		result = doc.getElementsByTagName("objects").item(0);
		return result;
	}

	public Node listS3Buckets(String accessKey, String secretKey, String region) {
		Node result = null;
		Document doc = null;
		Regions reg = null;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<buckets></buckets>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			reg = Regions.fromName(region);
			if (reg == null) {
				Node node = doc.createElement("error");
				node.setTextContent("Invalid region name: " + region);
				doc.getElementsByTagName("buckets").item(0).appendChild(node);
			}

			AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

			AmazonS3 s3client = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(reg).build();

			List<Bucket> buckets = s3client.listBuckets();
			for (Bucket bucket : buckets) {
				Node node = doc.createElement("bucket");
				node.setTextContent(bucket.getName());
				doc.getElementsByTagName("buckets").item(0).appendChild(node);
			}

		} catch (Exception e) {
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("buckets").item(0).appendChild(node);
		}

		result = doc.getElementsByTagName("buckets").item(0);
		return result;
	}


	/* 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	/*
	 * List Objects
	 */
	public Node listObjects(String accessKey, String secretKey, String region, String bucketName, String regex,
			boolean verbose) {
		Node result = null;
		Document doc = null;
		Regions reg = null;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<objects></objects>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			reg = Regions.fromName(region);
			if (reg == null) {
				Node node = doc.createElement("error");
				node.setTextContent("Invalid region name: " + region);
				doc.getElementsByTagName("objects").item(0).appendChild(node);
			}

			AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

			AmazonS3 s3client = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(reg).build();
			/*
			 * I've left in the 'WithPrefix' for future reference
			 */
			ListObjectsRequest lor = new ListObjectsRequest().withBucketName(bucketName).withPrefix("");

			ObjectListing objectListing = s3client.listObjects(lor);
			for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
				if (os.getKey().matches(regex)) {
					Node objectNode = doc.createElement("object");
					Node keyNode = doc.createElement("key");
					keyNode.setTextContent(os.getKey());
					objectNode.appendChild(keyNode);
					System.out.println("key = " + os.getKey());
					if (verbose) {
						Node bucketNameNode = doc.createElement("bucketNameNode");
						bucketNameNode.setTextContent(os.getBucketName());
						objectNode.appendChild(bucketNameNode);
						System.out.println("Bucket name = " + os.getBucketName());

						Node ETagNode = doc.createElement("ETag");
						ETagNode.setTextContent(os.getETag());
						objectNode.appendChild(ETagNode);
						System.out.println("ETag = " + os.getETag());

						Node sizeNode = doc.createElement("size");
						sizeNode.setTextContent(String.valueOf(os.getSize()));
						objectNode.appendChild(sizeNode);
						System.out.println("Size = " + String.valueOf(os.getSize()));

						Node storageClassNode = doc.createElement("storageClass");
						storageClassNode.setTextContent(os.getStorageClass());
						objectNode.appendChild(storageClassNode);
						System.out.println("Storage Class = " + os.getStorageClass());

						Node lastModifiedNode = doc.createElement("lastModified");
						lastModifiedNode.setTextContent(os.getLastModified().toString());
						objectNode.appendChild(lastModifiedNode);
						System.out.println("Last Modified = " + os.getLastModified().toString());
						
						 S3Object o = s3client.getObject(bucketName, os.getKey());
						 System.out.println("content type = " + o.getObjectMetadata().getContentType());

						Node ownerNode = doc.createElement("owner");
						Node idNode = doc.createElement("id");
						idNode.setTextContent(os.getOwner().getId());
						ownerNode.appendChild(idNode);
						Node displayNameNode = doc.createElement("displayName");
						displayNameNode.setTextContent(os.getOwner().getDisplayName());
						ownerNode.appendChild(displayNameNode);
						objectNode.appendChild(ownerNode);
						System.out.println("Owner = " + os.getOwner());
					}
					doc.getElementsByTagName("objects").item(0).appendChild(objectNode);
				}
			}

		} catch (Exception e) {
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("objects").item(0).appendChild(node);
		}

		result = doc.getElementsByTagName("objects").item(0);
		return result;
	}
	
	/*
	 * Copy Objects
	 */
	public Node copyObjects(String accessKey, String secretKey, String region, String sourceBucketName,
			String destBucketName, String sourceRegex, String destDirectory) {
		Node result = null;
		Document doc = null;
		Regions reg = null;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<objects></objects>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			reg = Regions.fromName(region);
			if (reg == null) {
				Node node = doc.createElement("error");
				node.setTextContent("Invalid region name: " + region);
				doc.getElementsByTagName("objects").item(0).appendChild(node);
			}

			AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

			AmazonS3 s3client = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(reg).build();
			/*
			 * I've left in the 'WithPrefix' for future reference
			 */
			ListObjectsRequest lor = new ListObjectsRequest().withBucketName(sourceBucketName).withPrefix("");

			ObjectListing objectListing = s3client.listObjects(lor);
			for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
				if (os.getKey().matches(sourceRegex)) {
					String filename = os.getKey().substring(os.getKey().lastIndexOf("/") + 1);
					CopyObjectRequest copyReq = new CopyObjectRequest(sourceBucketName, os.getKey(), destBucketName,
							destDirectory + filename);
					s3client.copyObject(copyReq);
					Node objectNode = doc.createElement("object");
					Node keyNode = doc.createElement("key");
					keyNode.setTextContent(os.getKey());
					objectNode.appendChild(keyNode);
					System.out.println("key = " + os.getKey());
					doc.getElementsByTagName("objects").item(0).appendChild(objectNode);
				}
			}

		} catch (Exception e) {
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("objects").item(0).appendChild(node);
		}

		result = doc.getElementsByTagName("objects").item(0);
		return result;
	}
	
	/*
	 * Move Objects
	 */
	public Node moveObjects(String accessKey, String secretKey, String region, String sourceBucketName,
			String destBucketName, String sourceRegex, String destDirectory) {
		Node result = null;
		Document doc = null;
		Regions reg = null;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<objects></objects>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			reg = Regions.fromName(region);
			if (reg == null) {
				Node node = doc.createElement("error");
				node.setTextContent("Invalid region name: " + region);
				doc.getElementsByTagName("objects").item(0).appendChild(node);
			}

			AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

			AmazonS3 s3client = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(reg).build();
			/*
			 * I've left in the 'WithPrefix' for future reference
			 */
			ListObjectsRequest lor = new ListObjectsRequest().withBucketName(sourceBucketName).withPrefix("");

			ObjectListing objectListing = s3client.listObjects(lor);
			for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
				if (os.getKey().matches(sourceRegex)) {
					String filename = os.getKey().substring(os.getKey().lastIndexOf("/") + 1);
					CopyObjectRequest copyReq = new CopyObjectRequest(sourceBucketName, os.getKey(), destBucketName,
							destDirectory + filename);
					s3client.copyObject(copyReq);
					DeleteObjectRequest delReq = new DeleteObjectRequest(sourceBucketName, os.getKey());
					s3client.deleteObject(delReq);
					Node objectNode = doc.createElement("object");
					Node keyNode = doc.createElement("key");
					keyNode.setTextContent(os.getKey());
					objectNode.appendChild(keyNode);
					System.out.println("key = " + os.getKey());
					doc.getElementsByTagName("objects").item(0).appendChild(objectNode);
				}
			}

		} catch (Exception e) {
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("objects").item(0).appendChild(node);
		}

		result = doc.getElementsByTagName("objects").item(0);
		return result;
	}
	
	/*
	 * Download Objects
	 */

	public Node downloadObjects(String accessKey, String secretKey, String region, String bucketName, String regex,
			String destinationDirectory) {
		Node result = null;
		Document doc = null;
		Regions reg = null;
		OutputStream outStream = null;

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			StringReader sr = new StringReader("<objects></objects>");
			InputSource is = new InputSource(sr);
			doc = db.parse(is);

			reg = Regions.fromName(region);
			if (reg == null) {
				Node node = doc.createElement("error");
				node.setTextContent("Invalid region name: " + region);
				doc.getElementsByTagName("objects").item(0).appendChild(node);
			}

			AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

			AmazonS3 s3client = AmazonS3ClientBuilder.standard()
					.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(reg).build();
			/*
			 * I've left in the 'WithPrefix' for future reference
			 */
			ListObjectsRequest lor = new ListObjectsRequest().withBucketName(bucketName).withPrefix("");

			ObjectListing objectListing = s3client.listObjects(lor);
			for (S3ObjectSummary os : objectListing.getObjectSummaries()) {
				if (os.getKey().matches(regex)) {
					GetObjectRequest gor = new GetObjectRequest(bucketName, os.getKey());

					String filename = os.getKey().substring(os.getKey().lastIndexOf("/") + 1);

					S3Object s3object = s3client.getObject(gor);
					S3ObjectInputStream inputStream = s3object.getObjectContent();
					byte[] buffer = new byte[inputStream.available()];
					inputStream.read(buffer);

					File targetFile = new File(destinationDirectory + "/" + filename);
					outStream = new FileOutputStream(targetFile);
					outStream.write(buffer);
					Node objectNode = doc.createElement("object");
					Node keyNode = doc.createElement("key");
					keyNode.setTextContent(os.getKey());
					objectNode.appendChild(keyNode);
					System.out.println("key = " + os.getKey());
					
					doc.getElementsByTagName("objects").item(0).appendChild(objectNode);
				}
			}

		} catch (Exception e) {
			String error = e.getMessage();
			error = error.replaceAll("&", "&amp;");
			error = error.replaceAll("<", "&lt;");
			error = error.replaceAll(">", "&gt;");
			Node node = doc.createElement("error");
			node.setTextContent(error);
			doc.getElementsByTagName("objects").item(0).appendChild(node);
		}

		result = doc.getElementsByTagName("objects").item(0);
		return result;

	}

}
