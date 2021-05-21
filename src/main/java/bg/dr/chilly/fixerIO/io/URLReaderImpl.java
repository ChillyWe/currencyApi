package bg.dr.chilly.fixerIO.io;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class URLReaderImpl implements URLReader {

	public URLReaderImpl() {
		super();
	}

	public String read(URL url) {
		StringBuilder inline = new StringBuilder();
		Scanner sc;
		try {
			sc = new Scanner(url.openStream());
			while (sc.hasNext()) {
				inline.append(sc.nextLine());
			}
			sc.close();
		} catch (IOException e) {
			// TODO: 5/21/21 handle exception
			log.info(e.getMessage());
		}
		return inline.toString();
	}
}