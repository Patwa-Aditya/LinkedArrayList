package com.cybergeass.utils.LinkedArrayList;

import java.io.Serializable;
import java.util.AbstractSequentialList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * @author Dinesh Saini
 * @version 0.2
 *
 */

@SuppressWarnings({ "unchecked", "unused", "hiding", "rawtypes" })
public class LinkedArrayList<E> extends AbstractSequentialList<E>implements List<E>, Deque<E>, Cloneable, Serializable {
	transient private int size = 0;
	transient private Node<E> first = null;
	transient private Node<E> last = null;
	private transient int bufferSize = 512;
	private static final long serialVersionUID = 123456789L;

	public LinkedArrayList() {
	}

	public LinkedArrayList(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public LinkedArrayList(Collection<? extends E> paramCollection) {
		this();
		addAll(paramCollection); // TODO FIXME
	}

	public LinkedArrayList(int bufferSize, Collection<? extends E> paramCollection) {
		this(bufferSize);
		addAll(paramCollection); // TODO FIXME
	}

	public int size() {
		return size;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	private void linkFirst(E paramE) {
		if (first == null) {
			first = last = new Node<E>(null, bufferSize, null);
			first.data[first.size++] = paramE;
			++size;
		} else if (first.data == null) { // It will never exists, only when, if
											// somehow user set data with null
			first.data = new Object[bufferSize];
			first.size = 0; // reset size
			first.data[first.size++] = paramE;
			++size;
		} else if (first.data.length == first.size) {// first node is full, then
														// we need a new one
			first = new Node<E>(null, bufferSize, first);
			first.data[first.size++] = paramE;
			++size;
		} else {
			first.data[first.size++] = paramE;
			++size;
		}
	}

	private void linkLast(E paramE) {
		if (last == null) {
			first = last = new Node<E>(null, bufferSize, null);
			last.data[last.size++] = paramE;
			++size;
		} else if (last.data == null) { // It will never exists, only when, if
										// somehow user set data with null
			last.data = new Object[bufferSize];
			last.size = 0; // reset size
			last.data[last.size++] = paramE;
			++size;
		} else if (last.data.length == last.size) {// Last node is full, then we
													// need a new one
			last = new Node<E>(last, bufferSize, null);
			last.data[last.size++] = paramE;
			++size;
		} else {
			last.data[last.size++] = paramE;
			++size;
		}
	}

	void linkBefore(E paramE, Node<E> paramNode) {
		// TODO FIXME
		throw new UnsupportedOperationException("Linking in between is not supported in " + this.getClass().getName());
	}

	private E unlinkFirst(Node<E> paramNode) {
		if (size == 0 || first == null) {
			throw new NoSuchElementException();
		} else if (first.data == null || first.size == 0) {
			if (first.next == null) { // that was the last node with no element,
										// raise exception after deleting it
				first.data = null;
				first = null;
				throw new NoSuchElementException();
			} else { // this condition should never happens
				first.next.prev = null; // delete current reference to first
										// node
				Node<E> localNode = first;
				first = first.next; // move to next Node
				localNode.data = null; // free the storage
				localNode.next = null; // delete reference to current list also
				localNode = null; // delete node
				return unlinkFirst(first); // doit recursive until we don't find
											// any
				// element
			}
		} else {
			E data = (E) first.data[--first.size];
			first.data[first.size] = null;
			size--;
			return data;
		}
	}

	private E unlinkLast(Node<E> paramNode) {
		if (size == 0 || last == null) {
			throw new NoSuchElementException();
		} else if (last.data == null || last.size == 0) {
			if (last.prev == null) { // that was the last node with no element,
										// raise exception after deleting it
				last.data = null;
				last = null;
				throw new NoSuchElementException();
			} else { // this condition should never happens
				last.prev.next = null; // delete current reference to last
										// node
				Node<E> localNode = last;
				last = last.prev; // move to prev Node
				localNode.data = null; // free the storage
				localNode.prev = null; // delete reference to current list also
				localNode = null; // delete node
				return unlinkLast(last); // doit recursive until we don't find
											// any
				// element
			}
		} else {
			E data = (E) last.data[--last.size];
			last.data[last.size] = null;
			size--;
			return data;
		}
	}

	E unlink(Node<E> paramNode) {
		// TODO FIXME
		throw new UnsupportedOperationException();
	}

	public E getFirst() {
		if (first == null)
			throw new NoSuchElementException();
		if (first.data != null && first.size > 0)
			return (E) first.data[first.size - 1];
		else {
			// FIXME
		}
		throw new NoSuchElementException();
	}

	public E getLast() {
		if (last == null)
			throw new NoSuchElementException();
		if (last.data != null && last.size > 0)
			return (E) last.data[last.size - 1];
		else {
			// FIXME
		}
		throw new NoSuchElementException();
	}

	public E removeFirst() {
		return (E) unlinkFirst(first);
	}

	public E removeLast() {
		return (E) unlinkLast(last);
	}

	public void addFirst(E paramE) {
		linkFirst(paramE);
	}

	public void addLast(E paramE) {
		linkLast(paramE);
	}

	public boolean contains(Object paramObject) {
		return indexOf(paramObject) != -1;
	}

	public boolean add(E paramE) {
		linkLast(paramE);
		return true;
	}

	public void defragList() {
		int trackFirstLoc = 0;
		Node<E> localnode = first;

		while (localnode != null) {
			if (localnode.data == null || localnode.size == 0) {
				if (localnode.prev != null)
					localnode.prev.next = localnode.next;
				if (localnode.next != null)
					localnode.next.prev = localnode.prev;
				Node tmp = localnode;
				localnode = localnode.next;

				// reconfigure first and last nodes
				if (tmp == first)
					first = localnode;
				if (tmp == last)
					last = tmp.prev;

				tmp.data = null;
				tmp.next = null;
				tmp.prev = null;
				tmp = null;
			} else if (localnode.size != localnode.data.length) {
				Node<E> localNxtNode = localnode.next;

				// remove the next empty nodes
				while (localNxtNode != null && localNxtNode.size == 0) {
					localNxtNode.data = null;
					// no explicit null check is needed, we already came from
					// there
					localNxtNode.prev.next = localNxtNode.next;

					if (localNxtNode.next != null)
						localNxtNode.next.prev = localNxtNode.prev;
					// reconfigure the last node
					else if (localNxtNode == last) {
						last = localNxtNode.prev;
					}

					Node tmp = localNxtNode;
					localNxtNode = localNxtNode.next;
					tmp.next = null;
					tmp.prev = null;
					tmp = null;

				}
				// current node is the last node, leave it as it is
				if (localNxtNode == null)
					break;

				// copy data to current node from next node
				trackFirstLoc = 0;

				// process the odd 5 spaces
				switch ((localnode.data.length - localnode.size) % 5) {
				case 4:
					if (localNxtNode.size > 0) {
						localnode.data[localnode.size++] = localNxtNode.data[trackFirstLoc++];
						localNxtNode.size--;
					} else {
						continue;
					}
				case 3:
					if (localNxtNode.size > 0) {
						localnode.data[localnode.size++] = localNxtNode.data[trackFirstLoc++];
						localNxtNode.size--;
					} else {
						continue;
					}
				case 2:
					if (localNxtNode.size > 0) {
						localnode.data[localnode.size++] = localNxtNode.data[trackFirstLoc++];
						localNxtNode.size--;
					} else {
						continue;
					}
				case 1:
					if (localNxtNode.size > 0) {
						localnode.data[localnode.size++] = localNxtNode.data[trackFirstLoc++];
						localNxtNode.size--;
					} else {
						continue;
					}
				}

				// process the 5 spaces in each iteration
				while (localnode.size < localnode.data.length && localNxtNode.size < localNxtNode.data.length) {
					switch ((localNxtNode.data.length - localNxtNode.size) % 5) {
					case 0:
						localnode.data[localnode.size++] = localNxtNode.data[trackFirstLoc++];
						localNxtNode.size--;
					case 4:
						localnode.data[localnode.size++] = localNxtNode.data[trackFirstLoc++];
						localNxtNode.size--;
					case 3:
						localnode.data[localnode.size++] = localNxtNode.data[trackFirstLoc++];
						localNxtNode.size--;
					case 2:
						localnode.data[localnode.size++] = localNxtNode.data[trackFirstLoc++];
						localNxtNode.size--;
					case 1:
						localnode.data[localnode.size++] = localNxtNode.data[trackFirstLoc++];
						localNxtNode.size--;
					}
				}

				if (localnode.size == localnode.data.length) {
					localnode = localNxtNode;
					if (localnode.size != 0 && trackFirstLoc != 0) {
						for (int i = 0; i < localnode.size; i++) {
							localnode.data[i] = localnode.data[trackFirstLoc + i];
						}
						for (int i = localnode.size; i < localnode.size + trackFirstLoc
								&& (localnode.size + trackFirstLoc) < localnode.data.length; i++) {
							localnode.data[i] = null;
						}
					}
				}
			}
		}
	}

	public boolean remove(Object paramObject) {

		return false;
	}

	private class Node<E> {
		Object[] data;
		Node<E> next;
		Node<E> prev;
		int size = 0;

		private Node() {
		}

		Node(Node<E> prev, int length, Node<E> next) {
			this.next = next;
			this.prev = prev;
			if (next != null)
				next.prev = this;
			if (prev != null)
				prev.next = this;
			this.data = new Object[length];
			this.size = 0;
		}

		Node(Node<E> prev, E[] data, Node<E> next) {
			this.data = data;
			this.size = 0;
			this.next = next;
			this.prev = prev;
			if (next != null)
				next.prev = this;
			if (prev != null)
				prev.next = this;

		}
	}

	@Override
	public Iterator<E> descendingIterator() {
		// TODO FIXME
		return null;
	}

	@Override
	public E element() {
		// TODO FIXME
		return null;
	}

	@Override
	public boolean offer(E e) {
		// TODO FIXME
		return false;
	}

	@Override
	public boolean offerFirst(E e) {
		// TODO FIXME
		return false;
	}

	@Override
	public boolean offerLast(E e) {
		// TODO FIXME
		return false;
	}

	@Override
	public E peek() {
		// TODO FIXME
		return null;
	}

	@Override
	public E peekFirst() {
		// TODO FIXME
		return null;
	}

	@Override
	public E peekLast() {
		// TODO FIXME
		return null;
	}

	@Override
	public E poll() {
		// TODO FIXME
		return null;
	}

	@Override
	public E pollFirst() {
		// TODO FIXME
		return null;
	}

	@Override
	public E pollLast() {
		// TODO FIXME
		return null;
	}

	@Override
	public E pop() {
		// TODO FIXME
		return null;
	}

	@Override
	public void push(E e) {
		// TODO FIXME
		
	}

	@Override
	public E remove() {
		// TODO FIXME
		return null;
	}

	@Override
	public boolean removeFirstOccurrence(Object o) {
		// TODO FIXME
		return false;
	}

	@Override
	public boolean removeLastOccurrence(Object o) {
		// TODO FIXME
		return false;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		// TODO FIXME
		return null;
	}
}
