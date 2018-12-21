package laborai.studijosktu;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;

/**
 * Rikiuojamos objektų kolekcijos - aibės realizacija dvejetainiu paieškos
 * medžiu.
 *
 * @užduotis Peržiūrėkite ir išsiaiškinkite pateiktus metodus.
 *
 * @author darius.matulis@ktu.lt
 *
 * @param <E> Aibės elemento tipas. Turi tenkinti interfeisą Comparable<T>, arba
 * per klasės konstruktorių turi būti paduodamas Comparator<T> interfeisą
 * tenkinantis objektas.
 */
public class BstSetKTU<E extends Comparable<E>> implements SortedSetADT<E>, Cloneable {

    // Medžio šaknies mazgas
    protected BstNode<E> root = null;
    // Medžio dydis
    protected int size = 0;
    // Rodyklė į komparatorių
    protected Comparator<? super E> c = null;

    /**
     * Sukuriamas aibės objektas DP-medžio raktams naudojant Comparable<T>
     */
    public BstSetKTU() {
        this.c = (e1, e2) -> e1.compareTo(e2);
    }

    /**
     * Sukuriamas aibės objektas DP-medžio raktams naudojant Comparator<T>
     *
     * @param c Komparatorius
     */
    public BstSetKTU(Comparator<? super E> c) {
        this.c = c;
    }

    /**
     * Patikrinama ar aibė tuščia.
     *
     * @return Grąžinama true, jei aibė tuščia.
     */
    @Override
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * @return Grąžinamas aibėje esančių elementų kiekis.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Išvaloma aibė.
     */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * Patikrinama ar aibėje egzistuoja elementas.
     *
     * @param element - Aibės elementas.
     * @return Grąžinama true, jei aibėje egzistuoja elementas.
     */
    @Override
    public boolean contains(E element) {
        if (element == null) {
            throw new IllegalArgumentException("Element is null in contains(E element)");
        }

        return get(element) != null;
    }

    /**
     * Aibė papildoma nauju elementu.
     *
     * @param element - Aibės elementas.
     */
    @Override
    public void add(E element) {
        if (element == null) {
            throw new IllegalArgumentException("Element is null in add(E element)");
        }

        root = addRecursive(element, root);
    }

    private BstNode<E> addRecursive(E element, BstNode<E> node) {
        if (node == null) {
            size++;
            return new BstNode<>(element);
        }

        int cmp = c.compare(element, node.element);

        if (cmp < 0) {
            node.left = addRecursive(element, node.left);
        } else if (cmp > 0) {
            node.right = addRecursive(element, node.right);
        }

        return node;
    }

    public boolean addAll(BstSetKTU<? extends E> c){
       for(E e: c){
           add(e);
       }
       return true;
    }

    private E higher(BstNode<E> currentNode, E e){
        if(currentNode.getLeft() != null && currentNode.compareTo(e) > 0)
        {
            E candidate = higher(currentNode.getLeft(), e);
            if(candidate.compareTo(e) <= 0)
            {
                return currentNode.element;
            }
            return candidate;
        } else if (currentNode.getRight() != null && currentNode.compareTo(e) < 0)
        {
            return higher(currentNode.getRight(), e);
        } else {
            return currentNode.element;
        }
    }

    public E higher(E e)
    {
        if(root != null){
            E ret = higher(root ,e);
            if (ret.compareTo(e) <= 0){
                return null;
            }
            else{
                return ret;
            }
        }
        else{
            return null;
        }
    }

    /**
     * Pašalinamas elementas iš aibės.
     *
     * @param element - Aibės elementas.
     */
    @Override
    public void remove(E element) {
        if (element == null) {
            throw new IllegalArgumentException("Element is null in remove(E element)");
        }

        root = removeRecursive(element, root);
    }

    private BstNode<E> removeRecursive(E element, BstNode<E> node) {
        if (node == null) {
            return node;
        }
        // Medyje ieškomas šalinamas elemento mazgas;
        int cmp = c.compare(element, node.element);

        if (cmp < 0) {
            node.left = removeRecursive(element, node.left);
        } else if (cmp > 0) {
            node.right = removeRecursive(element, node.right);
        } else if (node.left != null && node.right != null) {
            /* Atvejis kai šalinamas elemento mazgas turi abu vaikus.
             Ieškomas didžiausio rakto elemento mazgas kairiajame pomedyje.
             Galima kita realizacija kai ieškomas mažiausio rakto
             elemento mazgas dešiniajame pomedyje. Tam yra sukurtas
             metodas getMin(E element);
             */
            BstNode<E> nodeMax = getMax(node.left);
            /* Didžiausio rakto elementas (TIK DUOMENYS!) perkeliamas į šalinamo
             elemento mazgą. Pats mazgas nėra pašalinamas - tik atnaujinamas;
             */
            node.element = nodeMax.element;
            // Surandamas ir pašalinamas maksimalaus rakto elemento mazgas;
            node.left = removeMax(node.left);
            size--;
            // Kiti atvejai
        } else {
            node = (node.left != null) ? node.left : node.right;
            size--;
        }

        return node;
    }

    private E get(E element) {
        if (element == null) {
            throw new IllegalArgumentException("Element is null in get(E element)");
        }

        BstNode<E> node = root;
        while (node != null) {
            int cmp = c.compare(element, node.element);

            if (cmp < 0) {
                node = node.left;
            } else if (cmp > 0) {
                node = node.right;
            } else {
                return node.element;
            }
        }

        return null;
    }

    /**
     * Pašalina maksimalaus rakto elementą paiešką pradedant mazgu node
     *
     * @param node
     * @return
     */
    BstNode<E> removeMax(BstNode<E> node) {
        if (node == null) {
            return null;
        } else if (node.right != null) {
            node.right = removeMax(node.right);
            return node;
        } else {
            return node.left;
        }
    }

    /**
     * Grąžina maksimalaus rakto elementą paiešką pradedant mazgu node
     *
     * @param node
     * @return
     */
    BstNode<E> getMax(BstNode<E> node) {
        return get(node, true);
    }

    /**
     * Grąžina minimalaus rakto elementą paiešką pradedant mazgu node
     *
     * @param node
     * @return
     */
    BstNode<E> getMin(BstNode<E> node) {
        return get(node, false);
    }

    private BstNode<E> get(BstNode<E> node, boolean findMax) {
        BstNode<E> parent = null;
        while (node != null) {
            parent = node;
            node = (findMax) ? node.right : node.left;
        }
        return parent;
    }

    /**
     * Grąžinamas aibės elementų masyvas.
     *
     * @return Grąžinamas aibės elementų masyvas.
     */
    @Override
    public Object[] toArray() {
        int i = 0;
        Object[] array = new Object[size];
        for (Object o : this) {
            array[i++] = o;
        }
        return array;
    }

    /**
     * Aibės elementų išvedimas į String eilutę Inorder (Vidine) tvarka. Aibės
     * elementai išvedami surikiuoti didėjimo tvarka pagal raktą.
     *
     * @return elementų eilutė
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (E element : this) {
            sb.append(element.toString()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    /**
     *
     * Medžio vaizdavimas simboliais, žiūr.: unicode.org/charts/PDF/U2500.pdf
     * Tai 4 galimi terminaliniai simboliai medžio šakos gale
     */
    private static final String[] term = {"\u2500", "\u2534", "\u252C", "\u253C"};
    private static final String rightEdge = "\u250C";
    private static final String leftEdge = "\u2514";
    private static final String endEdge = "\u25CF";
    private static final String vertical = "\u2502  ";
    private String horizontal;


    //@Override
    public void add(String dataString) {

    }

    //@Override
    public void load(String fName) {

    }

    /* Papildomas metodas, išvedantis aibės elementus į vieną String eilutę.
     * String eilutė formuojama atliekant elementų postūmį nuo krašto,
     * priklausomai nuo elemento lygio medyje. Galima panaudoti spausdinimui į
     * ekraną ar failą tyrinėjant medžio algoritmų veikimą.
     *
     * @author E. Karčiauskas
     */
    public String toVisualizedString(String dataCodeDelimiter) {
        horizontal = term[0] + term[0];
        return root == null ? new StringBuilder().append(">").append(horizontal).toString()
                : toTreeDraw(root, ">", "", dataCodeDelimiter);
    }

    private String toTreeDraw(BstNode<E> node, String edge, String indent, String dataCodeDelimiter) {
        if (node == null) {
            return "";
        }
        String step = (edge.equals(leftEdge)) ? vertical : "   ";
        StringBuilder sb = new StringBuilder();
        sb.append(toTreeDraw(node.right, rightEdge, indent + step, dataCodeDelimiter));
        int t = (node.right != null) ? 1 : 0;
        t = (node.left != null) ? t + 2 : t;
        sb.append(indent).append(edge).append(horizontal).append(term[t]).append(endEdge).append(
                split(node.element.toString(), dataCodeDelimiter)).append(System.lineSeparator());
        step = (edge.equals(rightEdge)) ? vertical : "   ";
        sb.append(toTreeDraw(node.left, leftEdge, indent + step, dataCodeDelimiter));
        return sb.toString();
    }

    private String split(String s, String dataCodeDelimiter) {
        int k = s.indexOf(dataCodeDelimiter);
        if (k <= 0) {
            return s;
        }
        return s.substring(0, k);
    }

    /**
     * Sukuria ir grąžina aibės kopiją.
     *
     * @return Aibės kopija.
     * @throws java.lang.CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        BstSetKTU<E> cl = (BstSetKTU<E>) super.clone();
        if (root == null) {
            return cl;
        }
        cl.root = cloneRecursive(root);
        cl.size = this.size;
        return cl;
    }

    private BstNode<E> cloneRecursive(BstNode<E> node) {
        if (node == null) {
            return null;
        }

        BstNode<E> clone = new BstNode<>(node.element);
        clone.left = cloneRecursive(node.left);
        clone.right = cloneRecursive(node.right);
        return clone;
    }

    /**
     * Grąžinamas aibės poaibis iki elemento.
     *
     * @param element - Aibės elementas.
     * @return Grąžinamas aibės poaibis iki elemento.
     */
    @Override
    public SetADT<E> headSet(E element) {
        throw new UnsupportedOperationException("Studentams reikia realizuoti headSet()");
    }

    /**
     * Grąžinamas aibės poaibis nuo elemento element1 iki element2.
     *
     * @param element1 - pradinis aibės poaibio elementas.
     * @param element2 - galinis aibės poaibio elementas.
     * @return Grąžinamas aibės poaibis nuo elemento element1 iki element2.
     */
    @Override
    public SetADT<E> subSet(E element1, E element2) {
        return subSet(element1, true, element2, false);
    }
    public SetADT<E> subSet(E element1, boolean inclusive1, E element2, boolean inclusive2) {
        SetADT<E> ret = new BstSetKTU<E>();
        E el1 = element1, el2 = element2;
        if(element1.compareTo(element2) < 0)
        {
            el1 = element2;
            el2 = element1;
        }


        addBetween(root, el1, el2, ret, inclusive1, inclusive2);
        return ret;
    }

    private void addBetween
            (BstNode<E> subtree, E element1, E element2, SetADT<E> toAddTo, boolean inclusive1, boolean inclusive2)
    {
        if(subtree == null) return;
        if(inclusive1)
            if(subtree.element.compareTo(element1) == 0)
                toAddTo.add(subtree.element);
        if(inclusive2)
            if(subtree.element.compareTo(element2) == 0) {
                toAddTo.add(subtree.element);
            }
        if(subtree.element.compareTo(element1) > 0 && subtree.element.compareTo(element2) < 0)
            toAddTo.add(subtree.element);
        addBetween(subtree.left,element1, element2, toAddTo, inclusive1, inclusive2);
        addBetween(subtree.right, element1, element2, toAddTo, inclusive1, inclusive2);
    }
    /**
     * Grąžinamas aibės poaibis iki elemento.
     *
     * @param element - Aibės elementas.
     * @return Grąžinamas aibės poaibis nuo elemento.
     */
    @Override
    public SetADT<E> tailSet(E element) {
        throw new UnsupportedOperationException("Studentams reikia realizuoti tailSet()");
    }

    public int getHeight() { return height(root, 1);}

    private int height(BstNode<E> crrNode, int crrHeight) {
        if(crrNode == null) return crrHeight - 1;
        int candidate1 = height(crrNode.left, crrHeight + 1);
        int candidate2 = height(crrNode.right, crrHeight + 1);
        if(candidate1 > candidate2)
            return candidate1;
        else
            return candidate2;
    }


    /**
     * Grąžinamas tiesioginis iteratorius.
     *
     * @return Grąžinamas tiesioginis iteratorius.
     */
    @Override
    public Iterator<E> iterator() {
        return new IteratorKTU(true);
    }

    /**
     * Grąžinamas atvirkštinis iteratorius.
     *
     * @return Grąžinamas atvirkštinis iteratorius.
     */
    @Override
    public Iterator<E> descendingIterator() {
        return new IteratorKTU(false);

    }
    public E pollFirst() {
        if(root == null) {
            return null;
        }
        BstNode<E> retNode = root;
        BstNode<E> parent = null;
        while(retNode.left != null) {
            parent = retNode;
            retNode = retNode.left;
        }
        if(parent != null) {
            parent.left = retNode.right;
            return retNode.element;
        } else {
            root = retNode.right;
            return retNode.element;
        }
    }

    public E pollLast() {
        if(root == null) {
            return null;
        }
        BstNode<E> retNode = root;
        BstNode<E> parent = null;
        while(retNode.right != null) {
            parent = retNode;
            retNode = retNode.right;
        }
        if(parent != null) {
            parent.right = retNode.left;
            return retNode.element;
        } else {
            root = retNode.left;
            return retNode.element;
        }
    }
    /**
     * Vidinė objektų kolekcijos iteratoriaus klasė. Iteratoriai: didėjantis ir
     * mažėjantis. Kolekcija iteruojama kiekvieną elementą aplankant vieną kartą
     * vidine (angl. inorder) tvarka. Visi aplankyti elementai saugomi steke.
     * Stekas panaudotas iš java.util paketo, bet galima susikurti nuosavą.
     */
    private class IteratorKTU implements Iterator<E> {

        private Stack<BstNode<E>> stack = new Stack<>();
        // Nurodo iteravimo kolekcija kryptį, true - didėjimo tvarka, false - mažėjimo
        private boolean ascending;
        // Nurodo einamojo medžio elemento tėvą. Reikalingas šalinimui.
        private BstNode<E> parent = root;

        IteratorKTU(boolean ascendingOrder) {
            this.ascending = ascendingOrder;
            this.toStack(root);
        }

        @Override
        public boolean hasNext() {
            return !stack.empty();
        }

        @Override
        public E next() {
            if (!stack.empty()) {
                // Grąžinamas paskutinis į steką patalpintas elementas
                BstNode<E> n = stack.pop();
                // Atsimenama tėvo viršunė. Reikia remove() metodui
                parent = (!stack.empty()) ? stack.peek() : root;
                BstNode node = (ascending) ? n.right : n.left;
                // Dešiniajame n pomedyje ieškoma minimalaus elemento,
                // o visi paieškos kelyje esantys elementai talpinami į steką
                toStack(node);
                return n.element;
            } else { // Jei stekas tuščias
                return null;
            }
        }

        @Override
        public void remove() {
            // Node to remove is assumed to be non-null.
			BstNode<E> toRemove = ascending ? parent.left : parent.right; //n1
			BstNode<E> newChild = toRemove.right;	//n2
			BstNode<E> newChildLeft = toRemove.left;//n3
			BstNode<E> toReAdd = null;
			if (newChild != null && newChild.left != null)
				toReAdd = newChild.left;			//n6
			if (newChildLeft != null){
				newChild.left = newChildLeft;
			}
			if (ascending){
				parent.left = newChild;
			} else {
				parent.right = newChild;
			}
			if (toReAdd != null)
				reAddNode(newChild, toReAdd);
        }
        private void reAddNode(BstNode<E> subtree, BstNode<E> toAdd) {
		if(subtree.element.compareTo(toAdd.element) < 0){
			if(subtree.left == null){
				subtree.left = toAdd;
			} else {
				reAddNode(subtree.left, toAdd);
			}
		} else {
			if(subtree.right == null){
				subtree.right = toAdd;
			} else {
				reAddNode(subtree.right, toAdd);
			}
		}
	}

        private void toStack(BstNode<E> n) {
            while (n != null) {
                stack.push(n);
                n = (ascending) ? n.left : n.right;
            }
        }
    }

    /**
     * Vidinė kolekcijos mazgo klasė
     *
     * @param <N> mazgo elemento duomenų tipas
     */
    protected class BstNode<N extends Comparable> {

        // Elementas
        protected N element;
        // Rodyklė į kairįjį pomedį
        protected BstNode<N> left;
        // Rodyklė į dešinįjį pomedį
        protected BstNode<N> right;

        protected BstNode() {
        }

        protected BstNode(N element) {
            this.element = element;
            this.left = null;
            this.right = null;
        }

        public int compareTo(N e) { return element.compareTo(e);}
        public BstNode<N> getRight() {return right;}
        public BstNode<N> getLeft() {return left;}
    }

}
