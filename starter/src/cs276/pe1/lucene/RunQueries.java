package cs276.pe1.lucene;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cs276.util.IOUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class RunQueries {


	public static void main(String[] argv) throws Exception {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);

        while (true) {
            System.out.print("Enter query: ");
            String query = br.readLine();
			System.out.print("Enter field: ");
            String field = br.readLine();
            System.out.println(IMDBReader.runQueryForTitle(query, field));
        }
	}
}
