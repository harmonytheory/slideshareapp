package net.harmonytheory.android.slideshare.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.vvakame.util.jsonpullparser.JsonFormatException;
import net.vvakame.util.jsonpullparser.JsonPullParser;
import net.vvakame.util.jsonpullparser.JsonPullParser.State;
import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;
import net.vvakame.util.jsonpullparser.util.OnJsonObjectAddListener;
import net.vvakame.util.jsonpullparser.util.TokenConverter;
import android.util.SparseArray;

@JsonModel(decamelize = true, treatUnknownKeyAsError=false)
public class MetaJs {
	public MetaJs() {
	}
	@JsonKey(converter=AvailableSizesConverter.class)
	private SparseArray<SparseArray<List<Integer>>> availableSizes;
	public SparseArray<SparseArray<List<Integer>>> getAvailableSizes() {
		return availableSizes;
	}
	public void setAvailableSizes(
			SparseArray<SparseArray<List<Integer>>> availableSizes) {
		this.availableSizes = availableSizes;
	}
	
	public static class AvailableSizesConverter extends TokenConverter<SparseArray<SparseArray<List<Integer>>>> {

		static final AvailableSizesConverter conv = new AvailableSizesConverter();


		/**
		 * Returns an instance of {@link AvailableSizesConverter}.<br>
		 * NB: Implemented as singleton, as it has stateless nature.
		 * @return {@link AvailableSizesConverter}
		 * @author vvakame
		 */
		public static AvailableSizesConverter getInstance() {
			return conv;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public SparseArray<SparseArray<List<Integer>>> parse(JsonPullParser parser, OnJsonObjectAddListener listener)
				throws IOException, JsonFormatException {
			if (parser == null) {
				throw new IllegalArgumentException();
			}

			State state = parser.getEventType();

			switch (state) {
				case VALUE_NULL:
					return null;
				case START_HASH:
					SparseArray<SparseArray<List<Integer>>> availableSizeMap = parseAvailableSize(parser);
					if (listener != null) {
						listener.onAdd(availableSizeMap);
					}
					return availableSizeMap;
				default:
					throw new IllegalStateException();
			}
		}

		SparseArray<SparseArray<List<Integer>>> parseAvailableSize(JsonPullParser parser) throws IOException,
				JsonFormatException {
			SparseArray<SparseArray<List<Integer>>> availableSize = new SparseArray<SparseArray<List<Integer>>>();

			State state;
			String page = null;
			while ((state = parser.getEventType()) != State.END_HASH) {
				switch (state) {
					case KEY:
						page = parser.getValueString();
						break;
					case START_HASH:
						if (page == null) {
							throw new IllegalStateException("no such page.");
						}
						availableSize.put(Integer.parseInt(page), parsePageSize(parser));
						page = null;
						break;
					case END_HASH:
						break;
					default:
						throw new IllegalStateException();
				}
			}
			return availableSize;
		}

		SparseArray<List<Integer>> parsePageSize(JsonPullParser parser) throws IOException,
				JsonFormatException {
			SparseArray<List<Integer>> pageSize = new SparseArray<List<Integer>>();
			
			State state;
			String size = null;
			List<Integer> revisionList = null;
			while ((state = parser.getEventType()) != State.END_HASH) {
				switch (state) {
					case KEY:
						size = parser.getValueString();
						revisionList = new ArrayList<Integer>();
						break;
					case START_ARRAY:
						if (size == null || !revisionList.isEmpty()) {
							throw new IllegalStateException();
						}
						break;
					case VALUE_LONG:
						revisionList.add((int) parser.getValueLong());
						break;
					case END_ARRAY:
						pageSize.put(Integer.parseInt(size), revisionList);
						size = null;
						break;
					default:
						throw new IllegalStateException();
				}
			}
			return pageSize;
		}
	}
}
