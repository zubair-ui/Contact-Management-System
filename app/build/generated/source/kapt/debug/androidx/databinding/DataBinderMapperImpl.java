package androidx.databinding;

public class DataBinderMapperImpl extends MergedDataBinderMapper {
  DataBinderMapperImpl() {
    addMapper(new pk.edu.iqra.cms.DataBinderMapperImpl());
  }
}
