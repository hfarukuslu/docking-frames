package trader;

import java.util.List;

import com.google.inject.Module;

public interface InjectInit {

	void initMods(List<Module> list);

}
