package com.example.demo.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.example.demo.Domain.User;
import com.example.demo.Repository.UserRepository;


@Service
@Transactional
public class FileService {

	@Autowired
	private  UserRepository userRepository;
	
	public void saveFile(MultipartFile file) {
		String folder = "./temp/";
		try {
		byte[]	bytes = file.getBytes();
		Path path = Paths.get(folder + file.getOriginalFilename());
		String[] paths = StreamSupport.stream(path.spliterator(), false).map(Path::toString)
        .toArray(String[]::new);
		int length = paths.length - 1 ;
		String currentBasePath1 = "./temp" + java.io.File.separator;
		path = Paths.get(currentBasePath1 + paths[length]);
		checkAndCreateComponentPath(currentBasePath1);

		Files.write(path, bytes);
		getContainer(folder + paths[length]);
		Files.delete(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void getContainer(String FilePath) throws InvalidKeyException, URISyntaxException {
		// Create a BlobServiceClient object which will be used to create a container client
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(getConnection()).buildClient();

		String containerName = "devimages";

		BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
	
		BlobClient blobClient = containerClient.getBlobClient(FilePath);
		System.out.println("\nUploading to Blob storage as blob:\n\t" + blobClient.getBlobUrl());

		// Upload the blob
		
		blobClient.uploadFromFile(FilePath, true);
		
		User user = new User();
		user.setImageUrl(blobClient.getBlobUrl());
		user.setName("Test");
		userRepository.save(user);
	
	 
	}

	
	private String getConnection() {
		String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=storageaccticonciabh;AccountKey=0ErFHkv287yAElMSsxoPVKSvRFAI+wDKqkVE6k/Tkg13sZG9TS+2C+VvYwO6W2EFC/ff/1oQPvM1OmUdrTwSNA==;EndpointSuffix=core.windows.net";

		 return storageConnectionString;
		 
		}
		 
		public void getContainer() throws InvalidKeyException, URISyntaxException {
			// Create a BlobServiceClient object which will be used to create a container client
			BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(getConnection()).buildClient();

			//Create a unique name for the container
			String containerName = "devimages";
//			blobServiceClient.createBlobContainer(containerName).exists();
			// Create the container and return a container client object
			BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
			// Create a local file in the ./data/ directory for uploading and downloading
			String localPath = "/data/";
			String fileName = "quickstart" + java.util.UUID.randomUUID() + ".txt";
			File localFile = new File(localPath + fileName);

			// Write text to the file
			FileWriter writer;
			try {
				writer = new FileWriter(localPath + fileName, true);
				writer.write("Hello, World!");
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			BlobClient blobClient = containerClient.getBlobClient(fileName);
			//String containerNames = "devimages";
			// Get a reference to a blob
			

			System.out.println("\nUploading to Blob storage as blob:\n\t" + blobClient.getBlobUrl());

			// Upload the blob
			blobClient.uploadFromFile(localPath + fileName);
			
		
		 
		}
		public void checkAndCreateComponentPath(String currentBasePath) {
			java.io.File componentFile = new java.io.File(currentBasePath + java.io.File.separator );
			if (!componentFile.exists()) {
				if (!componentFile.mkdirs()) {
					//throw new ProgramException("");
				}
			}
		}

}
