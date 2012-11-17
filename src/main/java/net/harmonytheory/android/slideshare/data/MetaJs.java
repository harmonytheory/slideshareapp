package net.harmonytheory.android.slideshare.data;

import java.util.List;

import net.harmonytheory.android.slideshare.data.converter.AvailableSizesConverter;
import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;
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
}
