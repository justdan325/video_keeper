import java.util.ArrayList;
import java.util.Optional;

public class VideoList {
	private ArrayList<VideoDataNode> list;
	private int index = -1;

	public VideoList() {
		this.list = new ArrayList<>();
	}

	public VideoList(VideoList orig) {
		if (orig != null) {
			this.list = new ArrayList<>();

			if (orig.size() > 0) {
				for (int i = 0; i < orig.size(); i++) {
					Optional<VideoDataNode> curr = orig.peek(i);

					if (curr.isPresent()) {
						this.append(curr.get());
					}
				}

				this.index = orig.getIndex();
			}
		}
	}

	public synchronized void append(VideoDataNode node) {
		list.add(node);

		if (index < 0) {
			resetIndex();
		}
	}

	public synchronized void prepend(VideoDataNode node) {
		list.add(0, node);

		if (index != 0) {
			incrementIndex();
		} else {
			resetIndex();
		}
	}

	public synchronized boolean insert(int index, VideoDataNode node) {
		boolean added = false;

		if (index >= 0 && index <= this.index) {
			list.add(index, node);

			if (index <= this.index) {
				incrementIndex();
			}

			added = true;
		}

		return added;
	}

	public synchronized Optional<VideoDataNode> popCurr() {
		Optional<VideoDataNode> curr = Optional.empty();

		if (list.size() > 0) {
			curr = Optional.of(list.remove(index));

			//reset index to head if it walks off of end of list or if list is empty
			if (index > list.size() - 1) {
				resetIndex();
			}
		}

		return curr;
	}

	public synchronized Optional<VideoDataNode> pop(int index) {
		Optional<VideoDataNode> curr = Optional.empty();

		if (index >= 0 && index <= list.size()) {
			curr = Optional.of(list.remove(index));

			//reset index to head if it walks off of end of list or if list is empty
			if (index > list.size() - 1) {
				resetIndex();
			}
		}

		return curr;
	}

	public synchronized Optional<VideoDataNode> peekCurr() {
		Optional<VideoDataNode> curr = Optional.empty();

		if (list.size() > 0) {
			curr = Optional.of(list.get(index));
		}

		return curr;
	}

	public synchronized Optional<VideoDataNode> peekNext() {
		Optional<VideoDataNode> curr = Optional.empty();

		if (list.size() > 0) {
			if (index + 1 == list.size()) {
				curr = Optional.of(list.get(0));
			} else {
				curr = Optional.of(list.get(index + 1));
			}
		}

		return curr;
	}

	public synchronized Optional<VideoDataNode> peek(int index) {
		Optional<VideoDataNode> curr = Optional.empty();

		if (index >= 0 && index <= list.size()) {
			curr = Optional.of(list.get(index));
		}

		return curr;
	}

	public synchronized int getIndex() {
		return index;
	}

	public synchronized boolean setIndex(int index) {
		boolean set = false;

		if (index <= list.size() - 1) {
			this.index = index;
			set = true;
		}

		return set;
	}

	public synchronized void incrementIndex() {
		index++;

		if (index == list.size()) {
			index = 0;
		}
	}

	public synchronized void deccrementIndex() {
		index--;

		if (index < 0 && list.size() >= 0) {
			index = list.size() - 1;
		}
	}

	public synchronized int size() {
		return list.size();
	}

	public synchronized void clear() {
		list.clear();
		index = -1;
	}

	public synchronized void resetIndex() {
		if (list.size() == 0) {
			index = -1;
		} else {
			index = 0;
		}
	}

	public synchronized boolean contains(String url) {
		boolean contains = false;

		for (VideoDataNode node : list) {
			if (node.getUrl().equals(url)) {
				contains = true;
				break;
			}
		}

		return contains;
	}
}